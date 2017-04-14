program foof;
var fee, fi, fo, fum: integer;
function pTest( ha, hee, hoo : integer) : integer;
var gee, gi : integer;
begin
  gee := 10;
  gi := 1;
  pTest := (ha + gee - hoo) * (hee - gi)
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
      fo := pTest(10, 11, 10)
    else
      fo := 26
  ;
  write( fo)
end
.