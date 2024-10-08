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

        public God(int x, int y) {
            this.x=x;
            this.y=y;
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
        map[monster.centerX][monster.centerY]=3;
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
    }

    /**
     * 골렘의 중앙 위치 : 3
     * 골렘의 출구 위치 :2
     * 골렘의 중앙과 출구가 아닌 위치 : 1
     * 골렘이 없는 위치 :0
     *
     * 골렘의 출구와 중앙 위치(2|3)는 0이 아닌 위치를 모두 탐색할 수 있음(다음 골렘의 1, 다음 골렘의 출구 2, 다음 골렘의 중앙 위치 3(하지만 문제에서는 해당 위치를 만날 일이 없음))
     * 골렘의 출구와 중앙이 아닌 위치(1)는 골렘의 중앙 위치만 탐색할 수 있음
     * **/
    private static int moveGod(Monster monster) {
        int maxRow=Integer.MIN_VALUE;
        q=new LinkedList<>();
        visited=new boolean[R][C];
        q.add(new God(monster.centerX, monster.centerY));
        visited[monster.centerX][monster.centerY]=true;

        while(q.size()>0) {
            God now=q.poll();

            if(now.x>maxRow) maxRow= now.x;

            for(int d=0;d<deltas.length;d++) {

                int nx=now.x+deltas[d][0];
                int ny=now.y+deltas[d][1];

                // 벽을 넘어가는 경우는 없지만 확인
                if(nx<0||nx>=R||ny<0||ny>=C) continue;

                if(map[now.x][now.y]!=1&&map[nx][ny]!=0&&!visited[nx][ny]) {
                    q.add(new God(nx, ny));
                    visited[nx][ny]=true;
                } else if(map[now.x][now.y]==1&&map[nx][ny]==3&&!visited[nx][ny]) {
                    q.add(new God(nx, ny));
                    visited[nx][ny]=true;
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