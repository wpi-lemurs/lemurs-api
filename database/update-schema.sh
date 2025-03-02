#!/bin/bash

DBSTATUS=1

while [[ "$DBSTATUS" != "0" && $i -lt 60 ]]; do
	sleep 1
	i=$(($i+1))
	pg_isready -U $POSTGRES_USER -d $POSTGRES_DB
	DBSTATUS=$?
done

if [[ "$DBSTATUS" != "0" ]]; then 
	echo "PostgreSQL is taking more than 60 seconds to start up"
	exit 1
fi

# Run the update schema scripts as necessary.
if [ ! -e /var/lib/postgresql/data/schema.version ]; then
  echo "-0001" > /var/lib/postgresql/data/schema.version
fi

/usr/config/starting-owner.sh
current=`cat /var/lib/postgresql/data/schema.version`
for entry in /usr/config/updates/*
do
	index=${entry:20:4}
	test=${entry:25:4}
	if [[ $index -gt $current ]]; then
		if [[ $test != "TEST" || $POPULATE_TEST_DATA = "Y" ]]; then
			echo "Running update: $index"
			psql -U $POSTGRES_USER -d $POSTGRES_DB -f $entry
		else
			echo "Skipping test update: $index"
		fi
		current=$index
	fi
done

echo $current > /var/lib/postgresql/data/schema.version