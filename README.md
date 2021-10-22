# instapaper
Tool to remove duplicate links (and their surrounding li-tags) from Instapaper HTML export.

It's a command line program, which can be built and executed via the following commands:

```sh
lein do clean, uberjar
java -jar target/uberjar/instapaper.jar input-file-name.html output-file-name.html
```

Or simply use a `lein run`, as follows:
```sh
lein run input-file-name.html output-file-name.html
```

Also see blog article: https://photonsphere.org/posts-output/2019-02-18-instapaper-export/
