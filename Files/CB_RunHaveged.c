#include <sys/types.h> 
#include <unistd.h> 
#include <stdio.h>
#include <fcntl.h>

int main(int argc,char *argv[]) 
{

sigset_t mask;
sigfillset(&mask);
sigprocmask(SIG_SETMASK, &mask, NULL);

struct flock fl = {F_WRLCK, SEEK_SET,   0,      0,     0 };
fl.l_pid = getpid();

int pid_file = open("/dev/CB_RunHaveged.RUN", O_CREAT | O_RDWR, 0644);

int rc = fcntl(pid_file, F_SETLK, &fl);
if ( rc == -1 ) {
      printf("Couldn't lock PID file \"/dev/CB_RunHaveged.RUN\" for writing.\n");
    return 1;
}
 
pid_t pid; 
int fd[2];

if (pipe(fd) < 0) return 1;

//char *const parmList[] = {"/data/data/ch.waut/files/bin/haveged", "-F", "-o", "tbca8wbw", NULL}; 
//char *const parmList[] = {"/data/data/ch.waut/files/bin/haveged", "-F", "-o", "tba8cba8", NULL}; 
//char *const parmList[] = {"/data/data/ch.waut/files/bin/haveged", "-F", "-o", "ta8bcb", NULL}; 
char *const parmList[] = {"/data/data/ch.waut/files/bin/haveged", "-F", "-o", "tbcb", NULL}; 
//char *const parmList[] = {"/data/data/ch.waut/files/bin/haveged", "-F", "-o", "ta8wbwcbw", NULL}; 
//char *const parmList[] = {"/data/data/ch.waut/files/bin/haveged", "-F", NULL}; 
char *const envParms[2] = {"", NULL};
int i=1;
loop:
if ( i >=16 ) return 1; 
i++;
if ((pid = fork()) ==-1) 
  perror("fork error"); 
else if (pid == 0) 
 {
  close(STDIN_FILENO); 
  dup2(fd[0], STDIN_FILENO); 
  execve("/data/data/ch.waut/files/bin/haveged", parmList, envParms); 
  printf("Return not expected. Must be an execve error"); 
 }
 else { wait(NULL); sleep(i);goto loop; }
}

