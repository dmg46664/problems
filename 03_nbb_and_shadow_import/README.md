Attempting to launch from nbb without the class def uncommented

```
03_nbb_and_shadow_import  nbb launch_nbb.cljs
----- Error --------------------------------------
Message:  Could not resolve symbol: WebSocketServer
Phase:    analysis
Could not resolve symbol: WebSocketServer
```

Trying to compile and run shadow with the def line uncommented

```
03_nbb_and_shadow_import  yarn shadow-cljs compile script
yarn run v1.22.17
warning package.json: No license field
$ /Users/dmg46664/projects/problems/03_nbb_and_shadow_import/node_modules/.bin/shadow-cljs compile script
shadow-cljs - config: /Users/dmg46664/projects/problems/03_nbb_and_shadow_import/shadow-cljs.edn
[:script] Compiling ...
[:script] Build completed. (46 files, 1 compiled, 0 warnings, 1.12s)
✨  Done in 4.17s.
```


```
03_nbb_and_shadow_import  node out/test/test-daemon.js
file:///Users/dmg46664/projects/problems/03_nbb_and_shadow_import/out/test/cljs-runtime/load_server.js:3
shadow.esm.esm_import$ws.WebSocketServer = (function (){var obj6659 = shadow.js.shim.module$ws$default;
                                         ^

TypeError: Cannot assign to read only property 'WebSocketServer' of object '[object Module]'
    at file:///Users/dmg46664/projects/problems/03_nbb_and_shadow_import/out/test/cljs-runtime/load_server.js:3:42
    at ModuleJob.run (node:internal/modules/esm/module_job:193:25)
    at async Promise.all (index 0)
    at async ESMLoader.import (node:internal/modules/esm/loader:528:24)
    at async loadESM (node:internal/process/esm_loader:91:5)
    at async handleMainPromise (node:internal/modules/run_main:65:12)

Node.js v18.8.0
```

Shadow will work if we change the name of the def,
AND require `["ws" :as WebSocket]`

However NBB seems dead set on $default despite no default export from what
I can see https://github.com/websockets/ws/blob/master/index.js#L13
