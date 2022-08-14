package net.johanbasson.fp.core;

import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.function.Function;

public class Reader<C, A> {

    private Function<C, A> runner;

    private Reader(Function<C, A> runner) {
        this.runner = runner;
    }

    public static <C, A> Reader<C, A> of(Function<C, A> f) {
        return new Reader<>(f);
    }

    public static <C, A> Reader<C, A> pure(A a) {
        return new Reader<>(c -> a);
    }

    public static <C, A> Reader<C, List<A>> sequence(Iterable<Reader<C, A>> readers) {
        return new Reader<>(c -> {
            List<A> list = List.empty();
            for(Reader<C, A> r : readers) {
                list = list.append(r.apply(c));
            }
            return list;
        });
    }

    public A apply(C c) {
        return runner.apply(c);
    }

    public <U> Reader<C, U> map(Function<? super A, ? extends U> f) {
        return new Reader<>(c -> f.apply(apply(c)));
    }

    public <U> Reader<C, U> flatMap(Function<? super A, Reader<C, ? extends U>> f) {
        return new Reader<>(c -> f.apply(apply(c)).apply(c));
    }

    public <U> Reader<C, Tuple2<A, U>> zip(Reader<C, U> reader) {
        return this.flatMap(a -> reader.map(b -> new Tuple2<>(a, b)));
    }

}
