get-childitem -path C:\mediatrix_data\DMSftp\scanner1\* -recurse -include idx.dat | foreach -Process {Rename-Item $_ -NewName idx.txt}