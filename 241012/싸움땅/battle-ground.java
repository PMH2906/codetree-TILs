import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer tokens;
    static StringBuilder output = new StringBuilder();
    static int K, M, N, deltas[][]={{-1,0},{0,1},{1,0},{0,-1}},peopleMap[][];
    static People[] people;
    static PriorityQueue<Integer>[][] gun;

    public static class People{
        int x,y,d,s,p,g,num;
        People(int x,int y,int d,int s, int num ){
            this.x=x;
            this.y= y;
            this.d=d;
            this.s=s;
            this.num=num;
        }

    }
    public static void main(String[] args) throws IOException {

         tokens = new StringTokenizer(input.readLine());
         N = Integer.parseInt(tokens.nextToken());
         M = Integer.parseInt(tokens.nextToken());
         K = Integer.parseInt(tokens.nextToken());
        people = new People[M];
        peopleMap = new int[N][N];
        gun = new PriorityQueue[N][N];

         for(int r=0;r<N;r++){
             tokens = new StringTokenizer(input.readLine());
             for(int c=0;c<N;c++){
                 int g = Integer.parseInt(tokens.nextToken());
                 gun[r][c]=new PriorityQueue<>(Collections.reverseOrder());
                 if(g!=0) gun[r][c].add(g);
             }
         }

         for(int m=0;m<M;m++){
             tokens = new StringTokenizer(input.readLine());
             int x =  Integer.parseInt(tokens.nextToken())-1;
             int y =  Integer.parseInt(tokens.nextToken())-1;
             int d =  Integer.parseInt(tokens.nextToken());
             int s =  Integer.parseInt(tokens.nextToken());
             people[m]=new People(x, y, d, s,m+1);
             peopleMap[x][y]=m+1;
         }

         for(int k=0;k<K;k++){
             for(int m=0;m<M;m++){

                 People now = people[m];

                 // 이동
                 int nx = now.x+deltas[now.d][0];
                 int ny = now.y+deltas[now.d][1];
                 if(nx<0||nx>=N||ny<0||ny>=N){ // 반대로 방향 전환
                     now.d=(now.d+2)%4;
                     nx = now.x+deltas[now.d][0];
                     ny = now.y+deltas[now.d][1];
                 }
                 peopleMap[now.x][now.y]=0; // 그 전 위치를 0으로 해주기
                 now.x=nx; now.y=ny; // 사람 정보에 위치 변환

                 // 총 or 싸움
                 if( peopleMap[nx][ny]>0){ // 플레이어 있을 경우
                     People win, lose;
                     if(now.g+now.s>people[peopleMap[nx][ny]-1].g+people[peopleMap[nx][ny]-1].s){
                          win = now;
                          lose = people[peopleMap[nx][ny]-1];
                     }else if(now.g+now.s<people[peopleMap[nx][ny]-1].g+people[peopleMap[nx][ny]-1].s){
                          lose = now;
                          win = people[peopleMap[nx][ny]-1];
                     } else{
                         if(now.s>people[peopleMap[nx][ny]-1].s){
                              win = now;
                              lose = people[peopleMap[nx][ny]-1];
                         } else{
                              lose = now;
                              win = people[peopleMap[nx][ny]-1];
                         }
                     }

                     win.p += (win.s+win.g-(lose.s+lose.g)); // 이긴 사람 포인트 획득

                     // 진 사람 총 있다면 총 버리기
                     if(lose.g!=0) {
                         gun[nx][ny].add(lose.g);
                         lose.g = 0;
                     }

                     // 진 사람 이동
                     int loseNx = lose.x+deltas[lose.d][0];
                     int loseNy = lose.y+deltas[lose.d][1];
                     while(loseNx<0||loseNx>=N||loseNy<0||loseNy>=N|| peopleMap[loseNx][loseNy]>0){
                         lose.d=(lose.d+1)%4;
                         loseNx = lose.x+deltas[lose.d][0];
                         loseNy = lose.y+deltas[lose.d][1];
                     }
                     lose.x=loseNx; lose.y=loseNy;
                     peopleMap[loseNx][loseNy]=lose.num; // 진 사람 이동
                     peopleMap[nx][ny]=win.num; // 이긴 사람은 현재 위치 이동


                     if(gun[loseNx][loseNy].size()>0){ // 총 있을 경우
                         lose.g=gun[loseNx][loseNy].poll(); // 총 바꾸기
                     }

                     // 이긴 사람 총 바꾸기
                     if(win.g!=0){ // 이긴 사람 총이 있다면
                         gun[win.x][win.y].add(win.g);
                         win.g=gun[win.x][win.y].poll();
                     } else { // 없다면
                         if(gun[nx][ny].size()>0) win.g=gun[win.x][win.y].poll();
                     }


                 } else if(gun[nx][ny].size()>0){ // 총 있을 경우
                     if(now.g!=0){ // 현재 사람 총이 있다면
                         gun[nx][ny].add(now.g);
                         now.g=gun[nx][ny].poll();
                     } else { // 없다면
                         now.g=gun[nx][ny].poll();
                     }
                     peopleMap[nx][ny]=now.num;//이동
                 } else { // 총도 없고 플레이어도 없으면
                     peopleMap[nx][ny]=now.num;//이동
                 }

             }
         }

         for(int m=0;m<M;m++){
             output.append(people[m].p+" ");
         }

         System.out.print(output);
    }
}