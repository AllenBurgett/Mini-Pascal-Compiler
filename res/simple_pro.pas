program foop;
var fee, fi, fo, fum: integer;
procedure pTest( ha, hee, hoo : integer);
var gee, gi : integer;
begin
  gee := 10;
  gi := 1;
  fo := (ha + gee) * (hee - gi) / hoo
end
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
      pTest(10, 11, 10)
    else
      fo := 26
  ;
  write( fo)
end
.