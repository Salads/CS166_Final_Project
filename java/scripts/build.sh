#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


# compile the java program
javac -d $DIR/../classes $DIR/../src/GameRental.java

