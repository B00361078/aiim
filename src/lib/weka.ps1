[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$outpath = "C:/devtools/weka-3-8-5-azul-zulu-windows.exe"
$url = "https://sourceforge.net/projects/weka/files/weka-3-8/3.8.5/weka-3-8-5-azul-zulu-windows.exe/download?use_mirror=netcologne"
(New-Object System.Net.WebClient).DownloadFile($url, $outpath)
Start-Process -Wait -Filepath $outpath -ArgumentList '/S' -PassThru