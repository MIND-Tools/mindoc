#include <stdlib.h>
#include <stdio.h>

void METH(printerSrv, print)(const char * msg) {
  char* buffer = (char*) malloc(256);
  sprintf(buffer, "[proxy] %s", msg);
  CALL(printerCl, print)(buffer);
}

void METH(runner, run)() {
  //do nothing
}
