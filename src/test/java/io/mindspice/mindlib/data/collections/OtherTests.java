package io.mindspice.mindlib.data.collections;

import org.junit.jupiter.api.Test;


public class OtherTests {

    public record TestData(
            int collisionId
    ) {
        public boolean hasCollsion() {
            return collisionId != -1;
        }
    }

    @Test
    void derp() {
        TestData[] arr = new TestData[1000];
        for (int i = 0; i < 1000; ++i) {
            arr[i] = new TestData(i % 7);
        }

        TestData[][] arr2 = new TestData[5][5];

        long tTime = 0;
        long col = 0;

        for (int j = 0; j < 100_000; ++j) {
            for (int i = 0; i < 975; ++i) {
                long time = System.nanoTime();
                for (int x = 0; x < 5; ++x) {
                    for (int y = 0; y < 5; ++y) {
                        arr2[x][y] = arr[i];
                    }
                }
                for (int x = 0; x < 5; ++x) {
                    for (int y = 0; y < 5; ++y) {
                        if (arr2[x][y].hasCollsion()) {
                            col++;
                            break;
                        }
                    }
                }
                for (int x = 0; x < 5; ++x) {
                    for (int y = 0; y < 5; ++y) {
                        if (!arr2[x][y].hasCollsion()) {
                            col++;
                            break;
                        }
                    }
                }
                tTime += System.nanoTime() - time;
            }

        }
        System.out.println(tTime / col);
        System.out.println(col);
    }


}
