package io.mindspice.mindlib.data.collections;

import io.mindspice.mindlib.data.collections.lists.CyclicList;
import io.mindspice.mindlib.data.collections.lists.primative.IntList;
import io.mindspice.mindlib.data.collections.other.GridArray;
import org.junit.jupiter.api.Test;

import java.util.List;


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

    @Test
    void gridArray(){
        var ga = new GridArray<Object>(4,4);
        for (int i = 0; i < 4; ++i) {
            for(int j = 0; j < 4; ++ j){
                ga.set(i,j, new Object());
                System.out.println(ga.get(i,j));
            }
        }
    }

    @Test
    void cyclicListTest() {
        var lst = new CyclicList<>(List.of(0,1,2,3,4));

        for (int i = 0; i < 100; ++ i) {
            assert lst.getNext() == i % 5;
        }

        lst.remove(4);

        for (int i = 0; i < 100; ++ i) {
            assert lst.getNext() == i % 4;
        }

        assert lst.contains(1);

        lst.addAll(List.of(4,5,6,7,8,9,10));

        lst.resetIndex();

        for (int i = 0; i < 100; ++ i) {
            assert lst.getNext() == i % 11;

        }

        lst.remove(10);
        lst.resetIndex();

        for (int i = 0; i < 100; ++ i) {
            assert lst.getNext() == i % 10;
        }

        lst.removeAll(List.of(5, 6,7,8,9,10));

        lst.resetIndex();

        for (int i = 0; i < 100; ++ i) {
            assert lst.getNext() == i % 5;
        }

    }


}
