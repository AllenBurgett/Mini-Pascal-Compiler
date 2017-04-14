program foow;
var fee, fi, fo, fum: integer;
begin
  fee := 4;
  fi := 5;
  fo := 3 * fee + fi;
  while fo >= 13
    do
      fo := fo - 1
  ;
  if fo < 13
    then
      fo := 3 * fee + fi + 4 + 6 + 7 + 5 + 0 + 1
    else
      fo := 26
  ;
  write( fo)
end
.