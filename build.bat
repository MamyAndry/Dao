set "lib=E:\LIBRARY"

Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName } > src.txt

mkdir temp

$src = Get-Content src.txt

javac --source 8 --target 8 -cp "%lib%\postgresql.jar" -d temp $src

del src.txt

cd temp

jar -cf ..\Dao.jar .

cd ..

del -Recurse temp

move Dao.jar "%lib%"