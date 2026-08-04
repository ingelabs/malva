#define _POSIX_C_SOURCE 200809L

#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

/* Prints the number of every open file descriptor, one per line.
   This opens no file descriptors of its own, so anything it reports
   was inherited across exec(). */

int main(void) {
  long limit;
  long fd;

  limit = sysconf(_SC_OPEN_MAX);

  /* Use a safe default when OPEN_MAX is not set or pathologically large. */
  if (limit <= 0 || limit > 65536) {
    limit = 65536;
  }

  for (fd = 0; fd < limit; fd++) {
    if (fcntl((int)fd, F_GETFD) == -1) {
      if (errno == EBADF)
        continue;
      fprintf(stderr, "fcntl(%ld, F_GETFD): %s\n", fd, strerror(errno));
      return 2;
    }
    printf("%ld\n", fd);
  }

  return 0;
}
