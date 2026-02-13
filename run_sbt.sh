#!/usr/bin/env bash

die () {
    echo >&2 "$@"
    exit 1
}

scala2 () {
  printf "Scala 2\n\n"
  sbt "++ 2.13.16" $1
  printf "\n\nCompleted $1 for Scala 2\n\n"
}

scala3 () {
  printf "Scala 3\n\n"
  sbt "++ 3.3.7" $1
  printf "\n\nCompleted $1 for Scala 3\n\n"
}

# Check for nothing in $1 and show usage
[ "$#" -eq 2 ] || die "usage: run [sbt-cmd] [version]"

case $2 in
  2) scala2 $1
    ;;
  3) scala3 $1
    ;;
  both) scala2 $1; read -p "Press any key to continue to Scala 3..."; scala3 $1
    ;;
  *) die "$2 is not a supported version - valid values [2 | 3 | both]"
esac
