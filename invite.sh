#!/bin/bash

cmd='adb shell am start -a "android.intent.action.VIEW" -d '

if [ $# -eq 0 ]
then
  echo -e "$cmd\c"
  read url
else
  url="$1"
fi
$cmd "$url"
