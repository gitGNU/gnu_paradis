#!/bin/sh

## completion
. run.sh --completion--


## create directory for Java binaries
mkdir bin 2>/dev/null


## in with resources to bin/
cp -r res bin


## java compiler if default is for Java 7
[[ $(javac -version 2>&1 | cut -d . -f 2) = '7' ]] &&
    function javacSeven()
    {
	javac "$@"
    }

## java compiler if default is not for Java 7
[[ $(javac -version 2>&1 | cut -d . -f 2) = '7' ]] ||
    function javacSeven()
    {
	javac7 "$@"
    }


## warnings
warns='-Xlint:all'

## standard parameters
params='-source 7 -target 7 -s src -d bin'


## libraries
jars=`echo $(find lib | grep .jar$) | sed -e 's/lib\//:/g' -e 's/ //g'`


## parse options
for opt in "$@"; do
    if [[ $opt = '-ecj' ]]; then
	function javacSeven()
	{
            ecj -source 7 -target 7 "$@"
	}
    elif [[ $opt = '-echo' ]]; then
	function javacSeven()
	{
	    echo "$@"
	}
    elif [[ $opt = '-q' ]]; then
	warns=''
    fi
done


## compile paradis
javacSeven $warns -cp .$jars $params $(find src | grep '.java$') 2>&1

