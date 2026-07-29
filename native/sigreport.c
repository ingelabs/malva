#define _POSIX_C_SOURCE 200809L

#include <errno.h>
#include <signal.h>
#include <stdio.h>
#include <string.h>

struct named_signal {
  int number;
  const char *name;
};

#define SIGNAL_ENTRY(name) { name, #name }

static const struct named_signal signals[] = {
  SIGNAL_ENTRY(SIGABRT),
  SIGNAL_ENTRY(SIGALRM),
  SIGNAL_ENTRY(SIGBUS),
  SIGNAL_ENTRY(SIGCHLD),
  SIGNAL_ENTRY(SIGCONT),
  SIGNAL_ENTRY(SIGFPE),
  SIGNAL_ENTRY(SIGHUP),
  SIGNAL_ENTRY(SIGILL),
  SIGNAL_ENTRY(SIGINT),
  SIGNAL_ENTRY(SIGKILL),
  SIGNAL_ENTRY(SIGPIPE),
  SIGNAL_ENTRY(SIGQUIT),
  SIGNAL_ENTRY(SIGSEGV),
  SIGNAL_ENTRY(SIGSTOP),
  SIGNAL_ENTRY(SIGTERM),
  SIGNAL_ENTRY(SIGTSTP),
  SIGNAL_ENTRY(SIGTTIN),
  SIGNAL_ENTRY(SIGTTOU),
  SIGNAL_ENTRY(SIGUSR1),
  SIGNAL_ENTRY(SIGUSR2),
#ifdef SIGWINCH
  SIGNAL_ENTRY(SIGWINCH),
#endif
#ifdef SIGPOLL
  SIGNAL_ENTRY(SIGPOLL),
#endif
#ifdef SIGPROF
  SIGNAL_ENTRY(SIGPROF),
#endif
#ifdef SIGSYS
  SIGNAL_ENTRY(SIGSYS),
#endif
#ifdef SIGTRAP
  SIGNAL_ENTRY(SIGTRAP),
#endif
  SIGNAL_ENTRY(SIGURG),
#ifdef SIGVTALRM
  SIGNAL_ENTRY(SIGVTALRM),
#endif
#ifdef SIGXCPU
  SIGNAL_ENTRY(SIGXCPU),
#endif
#ifdef SIGXFSZ
  SIGNAL_ENTRY(SIGXFSZ),
#endif
  { -1, NULL }
};

int main(void) {
  sigset_t mask;
  int i;

  if (sigprocmask(SIG_SETMASK, NULL, &mask) == -1) {
    fprintf(stderr, "sigprocmask: %s\n", strerror(errno));
    return 1;
  }

  for (i = 0; signals[i].name != NULL; i++) {
    struct sigaction action;
    int blocked;

    if (sigaction(signals[i].number, NULL, &action) == -1) {
      if (errno == EINVAL)
        continue;
      fprintf(stderr, "sigaction(%s): %s\n", signals[i].name, strerror(errno));
      return 1;
    }

    blocked = sigismember(&mask, signals[i].number);
    if (blocked == -1) {
      fprintf(stderr, "sigismember(%s): %s\n", signals[i].name, strerror(errno));
      return 1;
    }

    printf("%s:", signals[i].name);
    if (blocked)
      printf(" blocked");
    if (action.sa_handler == SIG_IGN)
      printf(" ignore");
    else if (action.sa_handler == SIG_DFL)
      printf(" default");
    else
      printf(" caught");
    printf("\n");
  }

  return 0;
}
