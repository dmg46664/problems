
How to run:

Prerequistes:
https://github.com/Tensegritics/ClojureDart/blob/main/doc/flutter-quick-start.md
Steps 1 and 2

```
clj -M:cljd init
clj -M:flowstorm:cljd:repl/basic


```
https://github.com/Tensegritics/ClojureDart/blob/main/clj/src/cljd/compiler.cljc#L4699


https://github.com/Tensegritics/ClojureDart/blob/main/clj/src/cljd/compiler.cljc#L177


The error occurs while compiling namespace 'cljd.core

https://github.com/Tensegritics/ClojureDart/blob/main/clj/src/cljd/compiler.cljc#L177


https://github.com/Tensegritics/ClojureDart/blob/main/clj/src/cljd/compiler.cljc#L4699

https://github.com/Tensegritics/ClojureDart/blob/main/doc/README.md

Theres a bug in 'cljd.compiler/compile-namespace 'cljd.core

Open `src/flowstorm/flutter_debug.clj` and follow those instructions.
The file operated on is please-convert.cljd


https://github.com/Tensegritics/ClojureDart/blob/main/clj/src/cljd/compiler.cljc#L4683
bhttps://github.com/Tensegritics/ClojureDart/blob/main/clj/src/cljd/compiler.cljc#L4592



### Research to solve the bug

1. Record
2. Look down `cljd.compiler/recompile` code path
3. Add print to variable on `host-load-input`.

4. Search for `gensyms` but it's string matched!
5. Search for custom predicate `(fn [v] (= v 'gensyms))`
6. Can navigate to first `emit-def`
7. Going one up the stack to `host-eval` we can see 
 the macroexpand turns defn into a def
8. Return to `emit-def` to investigate further. Noting that it is
 step 22368. Has the macro already happened?
9. Looked through all the bindings `*host-eval*` looks interesting.
 Seems to mean we are in the `host-eval` function.
10. Equally knowing that there is a two pass system, looing at
    `*hosted*` which seems to have the main split in `do-def`
    Only bound https://github.com/Tensegritics/ClojureDart/blob/71b9d9f9c750c2aa332734418048a427ec19dac5/clj/src/cljd/build.clj#L200
    but is `true` when it mattered.
11. Power step forward into `do-def`
12. Instead tried starting at the exception, and then step to previous expression! Note that exceptions are also numerated expressions.
13. Think I see the issue. Eval the macro into hosted, when it hasn't been
loaded in hosted.
14. Adding `:tag PersistentVector` to include `macro-support` for vec causes error
15. Here it's best to go to the last flowstorm id (error) and just back step to `do-def`



```
(def
 ^{:macro-support true, :tag PersistentVector, :arglists (quote ([coll])), :schroupt (quote ([coll] ^{:line 6771, :column 3} (into [] coll))), :doc nil} vec
 ^{:rettag PersistentVector, :async nil}
 (fn* ([coll] ^{:line 6771, :column 3} (into [] coll))))
```
