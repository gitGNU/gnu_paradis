#!/bin/sh

## program execution information
package="se.kth.maandree.paradis"
hasMain=1
hasHome=1


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


## libraries
jars=''
if [ -d lib ]; then
    jars=`echo $(find lib | grep .jar$) | sed -e 's/lib\//:/g' -e 's/ //g'`
fi


## default runs
runs=''
if [[ $hasMain = 1 ]]; then
    runs+='main main-da'
    if [[ $hasHome = 1 ]]; then
	runs+='falsehome'
    fi
fi

## custom runs
runs+='chat multichat hubchat interfacechat plugins'


## default run
if [[ $# = 0 ]]; then
    javaSeven -ea -cp bin$jars "$package".Program


## custom runs

elif [[ $hasMain  &&  $1 = "main" ]]; then
    javaSeven -ea -cp bin$jars "$package".Program

elif [[ $hasMain  &&  $1 = "main-da" ]]; then
    javaSeven -da -cp bin$jars "$package".Program
    
elif [[ $hasMain  &&  $hasHome  &&  $1 = "falsehome" ]]; then
    __myhome=$HOME
    HOME='/dev/shm'
    javaSeven -ea -cp bin$jars "$package".Program
    HOME=$__myhome


## demo runs

elif [[ $1 = "chat" ]]; then
    javaSeven -ea -cp bin$jars "$package".demo.Chat

elif [[ $1 = "multichat" ]]; then
    javaSeven -ea -cp bin$jars "$package".demo.MultiChat

elif [[ $1 = "hubchat" ]]; then
    javaSeven -ea -cp bin$jars "$package".demo.HubChat

elif [[ $1 = "interfacechat" ]]; then
    javaSeven -ea -cp bin$jars "$package".demo.InterfaceChat

elif [[ $1 = "plugins" ]]; then
    javaSeven -ea -cp bin$jars "$package".demo.PluginDemo


## completion
elif [[ $1 = "--completion--" ]]; then
    _run()
    {
	local cur prev words cword
	_init_completion -n = || return
	
	COMPREPLY=( $( compgen -W "$runs" -- "$cur" ) )
    }
    
    complete -o default -F _run run

## missing rule
else
    echo "run: Rule missing.  Stop." >&2
fi
