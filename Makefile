# This make script is put in Public Domain
# Year: 2012, 2013
# Author: Mattias Andrée (maandree@member.fsf.org)


## COMPILE

all:
	@echo '`make all` is not yet implemented'


## INSTALL

install-var: install-var-dirs install-var-files

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
	touch     "/srv/paradis/packages.data"
	chmod 662 "/srv/paradis/packages.data"

install: all install-var


## MISC

uninstall:
	[[ -d "/var/spool/paradis/" ]] &&  rm -rf "/var/spool/paradis/"
	[[ -d "/srv/paradis/" ]] &&        rm -rf "/srv/paradis/"
	[[ -d "/var/paradis/" ]] &&        rm -rf "/var/paradis/"

clean:
	[[ -d "bin" ]] &&  rm -rf "bin"

