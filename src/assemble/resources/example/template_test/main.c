int main(void) {
  int i;
  int max = ATTR(nbRunners);
  if(max > 8)
    max = 8;
  for(i = 0; i < max; i++) {
    CALL(runners[i], run)();
  }
}
