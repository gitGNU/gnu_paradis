#!/bin/sh

## completion
. run.sh --completion--


## create directory for Java binaries
mkdir bin 2>/dev/null
chmod -R 755 bin


## in with resources to bin/
if [ -d res ]; then
    cp -r res bin
fi


## java compiler if default is for Java 7
[[ $(javac -version 2>&1 | cut -d . -f 2) = '7' ]] &&
    function javacSeven()
    {   javac "$@"
    }

## java compiler if default is not for Java 7
[[ $(javac -version 2>&1 | cut -d . -f 2) = '7' ]] ||
    function javacSeven()
    {   javac7 "$@"
    }


## java executer if default is for Java 7
[[ $(echo `java -version 2>&1 | cut -d . -f 2` | cut -d ' ' -f 1) = '7' ]] &&
    function javaSeven()
    {   java "$@"
    }

## java executer if default is not for Java 7
[[ $(echo `java -version 2>&1 | cut -d . -f 2` | cut -d ' ' -f 1) = '7' ]] ||
    function javaSeven()
    {   java7 "$@"
    }


## warnings
warns="-Xlint:all"

## standard parameters
params="-source 7 -target 7 -s src -d bin"


## libraries
jars=''
if [ -d lib ]; then
    jars=`echo $(find lib | grep .jar$) | sed -e 's/lib\//:lib\//g' -e 's/ //g'`
fi


## parse options
paramEcho=0
paramEcj=0
paramAnnot=0
paramDoc=0
for opt in "$@"; do
    if [[ $opt = '-ecj' ]]; then
	paramEcj=1
	if [ -d /opt/java7/jre/lib ]; then
	    function javacSeven()
	    {   ecj -bootclasspath `echo $(find /opt/java7/jre/lib | grep .jar$) | sed -e 's/\/opt\/java7\/jre\/lib\//:\/opt\/java7\/jre\/lib\//g' -e 's/ //g' | dd skip=1 bs=1 2>/dev/null` "$@"
	    }
	else
	    function javacSeven()
	    {   ecj "$@"
	    }
	fi
    elif [[ $opt = '-echo' ]]; then
	paramEcho=1
	function javacSeven()
	{   echo "$@"
	}
    elif [[ $opt = '-annot' ]]; then
	paramAnnot=1
    elif [[ $opt = '-doc' ]]; then
	paramDoc=1
    elif [[ $opt = '-q' ]]; then
	warns=''
    fi
done


if [[ $paramDoc = 1 ]]; then
    ## generate javadoc
    $docparams="-sourcepath src -source 7 -encoding utf-8 -version -author -charset utf-8 -linksource -sourcetab 8 -keywords -docencoding utf-8"
    javadoc7 $docparams -d doc/javadoc -private $(find src | grep '.java$') $(find bin | grep '.java$') ||
    javadoc  $docparams -d doc/javadoc -private $(find src | grep '.java$') $(find bin | grep '.java$')
else
    ## colouriser
    function colourise()
    {
	if [[ $paramEcho = 1 ]]; then
            cat
	elif [[ $paramEcj = 1 ]]; then
	    if [[ -f "colourpipe.ecj.jar" ]]; then
		javaSeven -jar colourpipe.ecj.jar
	    else
		cat
	    fi
	elif [[ -f "colourpipe.javac.jar" ]]; then
            javaSeven -jar colourpipe.javac.jar
	else
	    cat
	fi
    }
    
    ## exception generation
    if [ -f 'src/se/kth/maandree/javagen/ExceptionGenerator.java' ]; then
        ## compile exception generator
	( javacSeven $warns -cp . $params 'src/se/kth/maandree/javagen/ExceptionGenerator.java'  2>&1
	) | colourise &&
	
        ## generate exceptions code
	javaSeven -ea -cp bin$jars "se.kth.maandree.javagen.ExceptionGenerator" -o bin -- $(find src | grep '.exceptions$')  2>&1  &&
	echo -e '\n\n\n'  &&
	
        ## generate exceptions binaries
	( javacSeven $warns -cp bin$jars -source 7 -target 7 $(find bin | grep '.java$')  2>&1
	) | colourise
    fi &&
    
    ## compile annotations and annotation processorors
    ( javacSeven $warns -cp .:bin$jars $params src/se/kth/maandree/paradis/{ATProcessor,requires}.java  2>&1
    ) | colourise &&
    
    if [[ $paramAnnot = 0 ]]; then
        ## compile paradis
	( javacSeven $warns -cp .:bin$jars $params $(find src | grep '.java$')  2>&1
	) | colourise
    else
        ## run annotation processor (and compile paradis)
	javacSeven -processor se.kth.maandree.paradis.ATProcessor -processorpath bin -implicit:class -cp .:bin$jars $params $(find src | grep '.java$')
    fi
fi
