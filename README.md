# instapaper
Tool to remove duplicate links (and their surrounding li-tags) from Instapaper HTML export.

It's a command line program, which can be built and executed via the following commands:

```sh
lein do clean, uberjar
java -jar target/ubarjar/instapaper.jar input-file-name.html output-file-name.html
```
