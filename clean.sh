#!/bin/sh

# Copying and distribution of this file, with or without modification,
# are permitted in any medium without royalty provided the copyright
# notice and this notice are preserved.  This file is offered as-is,
# without any warranty.
# 
# [GNU All Permissive License]
# 
# Year: 2012, 2013
# Author: Mattias Andrée (maandree@member.fsf.org)


rm -r bin  2>  /dev/null

rm $(find . | grep '~$')        2>  /dev/null
rm $(find . | grep '\.class$')  2>  /dev/null
rm $(find . | egrep '/(.|)#')   2>  /dev/null

for file in $(find src/org/nongnu/paradis | grep '\.java$'); do
    expand $file > $file"~"
    mv $file"~" $file
    git add $file
done

