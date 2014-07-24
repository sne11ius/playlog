# Playlog

Simple blogware with Play Framework

## Features
  - Powered by [Play Framwork](http://www.playframework.com/)
  - Social sign in via github, google, twitter and facebook (powered by [silhouette](http://silhouette.mohiva.com/))
  - Atom feed
  - Awesome social share buttons
  - That's pretty much it ;)

## Build

This application depends on an unreleased version of the play-html-compressor
library. You will need to

  - Clone `https://github.com/sne11ius/play-html-compressor/tree/add_xml_compressor`
  - `publish-local` it using `sbt`

After this, you should

  - Configure your database connection via `conf/application.conf`
  - Configure your `application.context` via `conf/application.conf`
  - Create a `conf/silhouette.conf` and configure silhouette params

Finally, run `activator run` and hit `localhost:9000` to see whats coming up...

## License

Yep, it's GPL v3 - get over with it ;)

Also: see the [`LICENSE`](https://raw.githubusercontent.com/sne11ius/playlog/master/LICENSE) file
