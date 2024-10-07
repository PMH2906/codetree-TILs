import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

    static Queue<God> q=new LinkedList<>();
    static int[][] map, deltas={{-1,0},{0,1},{1,0},{0,-1}};
    static boolean[][] visited;
    static int ans=0, R, C, K;
    static Monster[] monsters;
    static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
    static StringBuilder output=new StringBuilder();
    static StringTokenizer tokens;

    static public class Monster{
        int centerX, centerY, gateDir;
        boolean possibleMoved;

        public Monster(int x, int y, int dir, boolean possibleMoved) {
            this.centerX=x;
            this.centerY=y;
            this.gateDir=dir;
            this.possibleMoved=possibleMoved;
        }
    }
    static public class God{
        int x, y;
        boolean isMoved;

        public God(int x, int y, boolean isMoved) {
            this.x=x;
            this.y=y;
            this.isMoved =isMoved;
        }
    }

    public static void main(String[] args) throws IOException {

        tokens=new StringTokenizer(input.readLine());
        R=Integer.parseInt(tokens.nextToken());
        C=Integer.parseInt(tokens.nextToken());
        K=Integer.parseInt(tokens.nextToken());

        map=new int[R][C];
        visited=new boolean[R][C];
        monsters=new Monster[K];

        for(int k=0;k<K;k++) {
            tokens=new StringTokenizer(input.readLine());
            int c=Integer.parseInt(tokens.nextToken());
            int dir=Integer.parseInt(tokens.nextToken());
            monsters[k]=new Monster(-2,c-1, dir, true);
            moveMonster(monsters[k]);

//            System.out.println("골렘 : "+k);
//            for(int r=0;r<R;r++) {
//                System.out.println(Arrays.toString(map[r]));
//            }
        }

        System.out.print(ans);
    }


    public static void moveMonster(Monster monster) {

        while (monster.possibleMoved) {
            if(move(monster.centerX+deltas[2][0], monster.centerY+deltas[2][1],0)) {
                monster.centerX+=deltas[2][0];
                monster.centerY+=deltas[2][1];
                continue;
            } else if(move(monster.centerX+deltas[3][0], monster.centerY+deltas[3][1],1)&&move(monster.centerX+deltas[3][0]+deltas[2][0],monster.centerY+deltas[3][1]+deltas[2][1],0)) {
                monster.centerX+=deltas[3][0]+deltas[2][0];
                monster.centerY+=deltas[3][1]+deltas[2][1];
                monster.gateDir=monster.gateDir-1<0?3:monster.gateDir-1; // 반시계
                continue;
            } else if(move(monster.centerX+deltas[1][0], monster.centerY+deltas[1][1],3)&&move(monster.centerX+deltas[1][0]+deltas[2][0],monster.centerY+deltas[1][1]+deltas[2][1],0)) {
                monster.centerX+=deltas[1][0]+deltas[2][0];
                monster.centerY+=deltas[1][1]+deltas[2][1];
                monster.gateDir=(monster.gateDir+1)%4; // 시계
                continue;
            }

            monster.possibleMoved=false;
        }

        // 골렘이 밖에 있으면 return
        if(monster.centerX<0) {
            map=new int[R][C];
            return;
        }
        map[monster.centerX][monster.centerY]=1;
        for(int d=0;d<deltas.length;d++) {

            // 모든방향확인
            int nx=monster.centerX+deltas[d][0];
            int ny=monster.centerY+deltas[d][1];

            // 골렘 넘어가면 map 초기화
            if(nx<0||nx>=R||ny<0||ny>=C) {
                map=new int[R][C];
                return;
            }

            if(d==monster.gateDir) map[nx][ny]=2;
            else map[nx][ny]=1;
        }

        ans+=moveGod(monster)+1;
//        System.out.println(ans);
    }

    private static int moveGod(Monster monster) {
        int maxRow=Integer.MIN_VALUE;
        q=new LinkedList<>();
        visited=new boolean[R][C];
        q.add(new God(monster.centerX, monster.centerY,true));
        visited[monster.centerX][monster.centerY]=true;

        while(q.size()>0) {
            God now=q.poll();

            if(now.x>maxRow) maxRow= now.x;

            for(int d=0;d<deltas.length;d++) {

                int nx=now.x+deltas[d][0];
                int ny= now.y+deltas[d][1];

                // 벽을 넘어가는 경우는 없지만 확인
                if(nx<0||nx>=R||ny<0||ny>=C) continue;

                // 다음 위치로 움직일 수 있는 경우에
                if(now.isMoved) {
                    // 1이면 출구가 아니기 때문에 다음 골렘 위치로 못 움직임
                    if(map[nx][ny]==1&&!visited[nx][ny]) {
                        q.add(new God(nx, ny, false));
                        visited[nx][ny]=true;
                    }
                    // 2이면 출구이므로 다음 골렘의 정령위치로 이동
                    if(map[nx][ny]==2&&!visited[nx][ny]) {
                        q.add(new God(nx, ny, false));
                        visited[nx][ny]=true;

                        for(int d2=0;d2<deltas.length;d2++) {
                            int nx2=nx+deltas[d2][0];
                            int ny2=ny+deltas[d2][1];

                            if(nx2<0||nx2>=R||ny2<0||ny2>=C) continue;

                            if(!visited[nx2][ny2]&&map[nx2][ny2]!=0) {
                                // 이어진 골렘의 정령위치로 가서 탐색
                                q.add(new God(nx2+deltas[d2][0], ny2+deltas[d2][1], true));
                                visited[nx2][ny2]=true;
                            }
                        }
                    }
                }
            }

        }

        return maxRow;
    }

    private static boolean move(int centerX, int centerY, int dir) {

        // 동, 남, 서 확인
        for(int d=0;d<deltas.length;d++) {
            if(dir==d) continue;

            int nx=centerX+deltas[d][0];
            int ny=centerY+deltas[d][1];

            // 벽 넘어가면 못 움직임
            if(nx>=R||ny<0||ny>=C) return false;

            // 골렘있으면 못 움직임
            if(nx>=0) if(map[nx][ny]!=0) return false;
        }

        return true;
    }
}