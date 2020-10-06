#!/bin/bash
echo "Eseguo 'mvn clean install' su tutti i progetti. I test sono ignorati."

find . -maxdepth 1 -type d \( ! -name . \) -exec bash -c "cd '{}' && mvn -DskipTests=true clean install" \;

echo "Ecco fatto!"

exit 0
