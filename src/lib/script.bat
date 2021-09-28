echo "executing powershell to download weka library jar"
Powershell.exe -ExecutionPolicy Bypass -File "weka.ps1"
echo "installing weka jar as dependency in m2 local repo"
mvn install:install-file -Dfile="C:\Program Files\Weka-3-8-5\weka.jar" -DgroupId=weka -DartifactId=weka -Dversion=3.8.5 -DgeneratePom=true -Dpackaging=jar