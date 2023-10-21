set "lib=E:\LIBRARY"

Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName } > src.txt

mkdir temp

$src = Get-Content src.txt

javac -cp "%lib%\postgresql.jar" -d temp $src

del src.txt

cd temp

jar -cf ..\Dao.jar .

cd ..

del -r temp

move Dao.jar "%lib%"