package io.mindspice.mindlib.shell;

import io.mindspice.mindlib.data.tuples.Pair;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ShellFactory;
import org.jline.builtins.ssh.ShellFactoryImpl;
import org.jline.builtins.ssh.Ssh;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import wrappers.KawaInstance;
import wrappers.functional.FuncRef;
import wrappers.functional.FuncType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.System.out;


public class SchemeShell {
    private final SshServer sshd;
    private final Map<String, FuncRef> procedures = new HashMap<>();
    private KawaInstance kawa;
    private ShellMode mode = ShellMode.COMMAND;
    private SchemeShellCompleter completer = new SchemeShellCompleter();
    private CommandInterface commandInterface;

    public SchemeShell(int port) throws IOException {
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));
        sshd.setPasswordAuthenticator(new AuthInstance());
        sshd.setShellFactory(createShellFactory());
        sshd.start();
    }

    public SchemeShell(int port, CommandInterface commandInterface, KawaInstance kawa) throws IOException {
        this.kawa = kawa;
        this.commandInterface = commandInterface;
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));
        sshd.setPasswordAuthenticator(new AuthInstance());
        sshd.setShellFactory(createShellFactory());
        sshd.start();
    }


    private static class AuthInstance implements PasswordAuthenticator {
        @Override
        public boolean authenticate(String username, String password, ServerSession session) {
            return "user".equals(username) && "password".equals(password);
        }
    }

    public String modePrompt(SchemeShellCompleter completer, ShellMode mode) {
        completer.setMode(mode);
        switch (mode) {
            case COMMAND -> { return "#|> "; }
            case SCHEME -> { return "λ|> "; }
            case SYSTEM -> { return "⛘:> "; }
            default -> { return "#|> "; }
        }
    }

    public ShellFactory createShellFactory() {
        Consumer<Ssh.ShellParams> shellLogic = shellParams -> {
            try {
                Terminal terminal = shellParams.getTerminal();
                OutputStream out = terminal.output();
                DirectoryManager dirManager = new DirectoryManager(terminal);
                completer.loadSchemeCompletions(kawa);

                LineReader reader = LineReaderBuilder.builder()
                        .terminal(terminal)
                        .history(new DefaultHistory())
                        .highlighter(new DefaultHighlighter())
                        .completer(completer)
                        .option(LineReader.Option.AUTO_FRESH_LINE, true)
                        .option(LineReader.Option.HISTORY_IGNORE_DUPS, true)
                        .option(LineReader.Option.AUTO_MENU, true)
                        .option(LineReader.Option.MENU_COMPLETE, true)
                        // .option(LineReader.Option.LIST_AMBIGUOUS, true)
                        .option(LineReader.Option.LIST_PACKED, true)
                        .build();

                Map<String, Pair<FuncType, Object>> procedures = new HashMap<>();

                terminal.puts(InfoCmp.Capability.clear_screen);
                terminal.flush();

                while (true) {
                    final String line = reader.readLine(modePrompt(completer, mode));
                    String output = "";
                    switch (line) {
                        case String s when "exit".equalsIgnoreCase(s.trim()) -> shellParams.getSession().close();
                        case String s when "help".equalsIgnoreCase(s.trim()) -> output = helpString;
                        case String s when "scheme".equalsIgnoreCase(s.trim()) -> mode = ShellMode.SCHEME;
                        case String s when "command".equalsIgnoreCase(s.trim()) -> mode = ShellMode.COMMAND;
                        case String s when "system".equalsIgnoreCase(s.trim()) -> mode = ShellMode.SYSTEM;
                        case String s when "clear".equalsIgnoreCase(s.trim()) -> {
                            terminal.puts(InfoCmp.Capability.clear_screen);
                            terminal.flush();
                        }
                        default -> {
                            switch (mode) {
                                case COMMAND -> {
                                    if (commandInterface == null) {
                                        output = "Error: No command interface assigned";
                                        break;
                                    }
                                    output = parseCommand(line);
                                }
                                case SYSTEM -> output = dirManager.processCommand(line);
                                case SCHEME -> output = schemeCommand(line);
                            }
                        }
                    }
                    if (output.isEmpty()) { continue; }
                    out.write(("=:> " + output.replace("\n", "\n=:> ")).getBytes());
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Throwable e) {
                try {
                    out.write(("Error: " + e.getMessage()).getBytes());
                } catch (IOException ex) { /*Ignored*/}
            }
        };

        return new ShellFactoryImpl(shellLogic);
    }

    private String schemeCommand(String line) {
        if (line.startsWith("scheme")) {
            line = line.replace("scheme", "");
        }
        String[] input = line.split(" ");

        switch (input[0]) {
            case String s when "--reload-auto-complete".equalsIgnoreCase(s) -> completer.loadSchemeCompletions(kawa);
            case String s when "--load-procedure".equalsIgnoreCase(s) -> {
                if (input.length != 3) { return "Error: Invalid number of arguments, expected 2"; }
                return loadSchemeProcedure(input[1], input[2]);
            }
            case String s when "--list-procedures".equalsIgnoreCase(s) -> {
                return procedures.keySet().stream().map(funcRef -> "\n" + funcRef).toString();
            }

            default -> {
                var result = kawa.safeEval(line);
                if (result.exception().isPresent()) {
                    return result.exception().get().toString();
                } else {
                    return result.result().isPresent()
                            ? result.result().get().toString()
                            : "No Error encounter, No Result Returned";
                }
            }
        }
        return "Could not parse: " + line;
    }

    private String loadSchemeProcedure(String funcType, String funcName) {
        FuncType funcEnum = FuncType.fromString(funcType);
        if (funcEnum == null) {
            return ("Error: Failed enum value lookup for: " + funcType);
        }
        out.println("func-enum:" + funcEnum);

        var result = kawa.safeEval(funcName);
        if (result.exception().isPresent() || result.result().isEmpty()) {
            return result.exception().get().getMessage();
        }

        procedures.put(funcName, FuncRef.of(funcEnum, result.result().get()));
        return ("Stored scheme procedure: " + funcName);
    }

    private String parseCommand(String input) {
        out.println(input);
        return input;
    }

    public String helpString = """
                        
            ##############
            ## Commands ##
            ##############
                        
            "command"
              -> Switch to command console.
              
            "scheme"
              -> Switch to scheme repl
              -> Flags --reload-auto-complete --load-procedure <Func-Interface-Enum> <Func-Definition-Name>"
              
             "browse"
              -> Switch to file browser and editor
              -> Sub commands: ls, cd, mv, rm, mkdir, cp, touch, nano
              
            "exit" 
              -> Exit and close connection
              
            "clear"
              -> Clear screen
            """;


}
