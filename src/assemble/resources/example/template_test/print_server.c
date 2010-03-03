#include <stdio.h>

void METH(printerSrv, print)(const char * msg) {
  printf("%s\n", msg);
}
