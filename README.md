# grimmly

A featherweight URL shortener written in clojure for reasons that I can't quite
fathom. It stores all information in memory using a fixed length buffer, which
drops the oldest record when inserting after the maximum length has been
reached.

This is mega alpha stuff - I don't recommend using this publicly facing just
yet, as there is no validation of input.

## Installation

Requires leinengen

    git clone https://github.com/oholiab/grimmly
    cd grimmly
    [OPTION=value,..] lein run

Or alternatively you can compile into an uberjar with

    cd grimmly
    lein uberjar

And supply options using

    java -Doption=value -jar path_to_jar

Options are:

    option [default]: description
    ip [127.0.0.1]  : The IP address the server should listen on
    port [8080]     : The port the server should listen on

## Usage

To add a URL, simply make a POST with only the URL in the body, e.g:

    curl -v -d "https://grimmwa.re" localhost:8080

Which will return a short sha. To get, hit up

    http://localhost:8080/<SHA>

And you will be 302'd to the address

## As Weechat URL shortener

I wrote this mostly for use with weechat in tmux where the setup of the windows
can really screw with text selection, so the idea was to URL shorten so that all
URLs could appear on one line. With this in mind, if you run grimmly within the
provided Docker image with:

    make run

It will both run the container in daemon mode and create a bridge network called
`grimmly_net` which you can then add to any other containers - the grimmly
container will then be accessible by the hostname "grimmly" on the container's
local resolver.

This means that if you're running a webserver, say nginx, in a docker container
for your website, you can then add a location stanza to that nginx:

    location /shrt/ {                        
      resolver 127.0.0.11 valid=5s;       
      error_page   404      /404.html;    
      if ($ssl_client_verify != SUCCESS) {
        return 404;                       
      }                                   
      set $upstream grimmly;              
      rewrite ^/s/(.*) /$1 break;         
      proxy_pass http://$upstream:8080;   
      proxy_redirect off;                 
    }                                     

In the above example I've used ssl client certificates to authenticate - please
ensure you add some transport encryption and authentication! If you tell me you
got owned because you left an open key-value store exposed to the internet I
will laugh at you!

To attach the containers together to make the above work:

    docker network connect grimmly_net <YOUR_NGINX_CONTAINER>

To install the weechat script

    cp weechat/grimmly.py ~/.weechat/python/

Then, assuming you're running your weechat session on the same server, you can
set the private server option to `http://localhost:8080` and the public server
option to `https://your-host.com/shrt/` and your weechat will shorten all urls
that come in, post them to `localhost:8080` and give you the shortened URL as
`https://your-host.com/shrt/<SHA>`

## Options

The buffer length is fixed in size (5 records at the time of writing for fast
array-map lookup and because it's a number I typed.)
