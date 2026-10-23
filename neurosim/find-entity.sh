#!/bin/bash
# find svg files containing a particular entity name
find -iname '*.svg' -exec grep -H "$1" {} \;

