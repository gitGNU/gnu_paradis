# This make script is put in Public Domain
# Year: 2012
# Author: Mattias Andrée (maandree@kth.se)


## COMPILE

all:
	@echo '`make all` is not yet implemented'


## INSTALL

install-var-dirs:
	install -d -m 7773 "/var/spool/paradis/packages/downloaded/"
	chown   ":users"   "/var/spool/paradis/packages/downloaded/"
	install -d -m 7773 "/srv/paradis/"
	chown   ":users"   "/srv/paradis/"
	install -d -m 7773 "/srv/paradis/packages/"
	chown   ":users"   "/srv/paradis/packages/"
	install -d -m 7773 "/var/paradis/installed/"
	chown   ":users"   "/var/paradis/installed/"

install-var-files:
	install -m 662 "/srv/paradis/packages.data"

install: all install-var-dirs install-var-files


## MISC

uninstall:
	rm -rf "/var/spool/paradis/"
	rm -rf "/srv/paradis/"
	rm -rf "/var/paradis/"

clean:
	rm -rf bin

