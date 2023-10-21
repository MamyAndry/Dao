lib=$HOME/Documents/LIBRARY

find -name '*.java' > src.txt

mkdir temp

javac -cp $lib/postgres.jar -d temp @src.txt

rm src.txt  

cp $lib/gson.jar temp/gson.jar

cd temp

jar -cf ../Dao.jar .
cd ../
rm -r temp
mv Dao.jar $lib
