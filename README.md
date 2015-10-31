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
    lein run

## Usage

To add a URL, simply make a POST with only the URL in the body, e.g:

    curl -v -d "https://grimmwa.re" localhost:8080

Which will return a short sha. To get, hit up

    http://localhost:8080/<SHA>

And you will be 302'd to the address

## Options

The buffer length is fixed in size (5 records at the time of writing for fast
array-map lookup and because it's a number I typed.
