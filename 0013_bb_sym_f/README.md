This is a skeleton copy of a private repo to demonstrate what I think is a 
a bug in
https://github.com/babashka/cli


Just run
```
bb extract-select```

```
{:clojure.main/message
   2   │  "Execution error (AssertionError) at babashka.cli.exec/-main (exec.clj
       │ :57).\nAssert failed: Could not find var: \nf\n",
   3   │  :clojure.main/triage
   4   │  {:clojure.error/class java.lang.AssertionError,
   5   │   :clojure.error/line 57,
   6   │   :clojure.error/cause "Assert failed: Could not find var: \nf",
   7   │   :clojure.error/symbol babashka.cli.exec/-main,
   8   │   :clojure.error/source "exec.clj",
   9   │   :clojure.error/phase :execution},
  10   │  :clojure.main/trace
  11   │  {:via
  12   │   [{:type java.lang.AssertionError,
  13   │     :message "Assert failed: Could not find var: \nf",
  14   │     :at [babashka.cli.exec$_main invokeStatic "exec.clj" 57]}],
  15   │   :trace
  16   │   [[babashka.cli.exec$_main invokeStatic "exec.clj" 57]
  17   │    [babashka.cli.exec$_main doInvoke "exec.clj" 17]
  18   │    [clojure.lang.RestFn applyTo "RestFn.java" 137]
  19   │    [clojure.lang.Var applyTo "Var.java" 705]

```

## Installation

 of a private 
