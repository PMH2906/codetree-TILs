import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {

    static StringBuilder output = new StringBuilder();
    static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer tokens;
    static Santa [] santas;
    static Rudolph rudolph;
    static int[][] isSanta, deltas ={{-1,0},{0,1},{1,0},{0,-1},{-1,-1},{-1,1},{1,-1},{1,1}}; // x,y,산타번호
    static boolean[][] isRudolph;
    static int N, M, P, C, D, failSanta=0;

    public static class Santa {
        int x, y, score=0, power, sleepTern;
        boolean fail, sleep;

        public Santa(int x, int y, int power) {
            this.x=x;
            this.y=y;
            this.power=power;
        }
    }
    public static class Rudolph {
        int x, y, power;

        public Rudolph(int x, int y, int power) {
            this.x=x;
            this.y=y;
            this.power=power;
        }
    }
    public static class MoveRudolph implements Comparable<MoveRudolph> {
        int x, y, dist, nx, ny, direct; // nx,ny는 루돌프가 올길 위치

        public MoveRudolph(int x, int y, int dist) {
            this.x=x;
            this.y=y;
            this.dist=dist;
        }

        public MoveRudolph(int x, int y, int dist, int nx, int ny, int direct) {
            this.x=x;
            this.y=y;
            this.dist=dist;
            this.nx=nx;
            this.ny=ny;
            this.direct=direct;
        }

        @Override
        public int compareTo(MoveRudolph o) {

            if(this.dist==o.dist) {
                if(this.x==o.x) {
                    return Integer.compare(this.y,o.y)*-1;
                }
                return Integer.compare(this.x,o.x)*-1;
            }
            return Integer.compare(this.dist, o.dist);
        }
    }
    public static class MoveSanta implements Comparable<MoveSanta> {
        int direct, dist, nx, ny;

        public MoveSanta(int direct, int dist, int nx, int ny) {
            this.direct=direct;
            this.dist=dist;
            this.nx=nx;
            this.ny=ny;
        }

        @Override
        public int compareTo(MoveSanta o) {

            if(this.dist==o.dist) {
                return Integer.compare(this.direct,o.direct);
            }
            return Integer.compare(this.dist, o.dist);
        }
    }

    public static void main(String[] args) throws IOException {

        tokens=new StringTokenizer(input.readLine());
        N=Integer.parseInt(tokens.nextToken());
        M=Integer.parseInt(tokens.nextToken());
        P=Integer.parseInt(tokens.nextToken());
        C=Integer.parseInt(tokens.nextToken());
        D=Integer.parseInt(tokens.nextToken());

        //초기화
        isRudolph=new boolean[N][N];
        isSanta=new int[N][N];
        for(int n=0;n<N;n++) {
            Arrays.fill(isSanta[n],-1);
        }
        santas=new Santa[P];

        tokens=new StringTokenizer(input.readLine());
        int r=Integer.parseInt(tokens.nextToken());
        int c=Integer.parseInt(tokens.nextToken());
        rudolph=new Rudolph(r-1,c-1,C);
        isRudolph[r-1][c-1]=true; // 루돌프 위치 넣기


        for(int p=0;p<P;p++) {
            tokens=new StringTokenizer(input.readLine());
            int num=Integer.parseInt(tokens.nextToken());
            r=Integer.parseInt(tokens.nextToken());
            c=Integer.parseInt(tokens.nextToken());
            santas[num-1]=new Santa(r-1,c-1,D);
            isSanta[r-1][c-1]=num-1;
        }

//        System.out.println("산타" );
//        for(int i=0;i<N;i++) {
//            System.out.println(Arrays.toString(isSanta[i]));
//        }
//        System.out.println("루돌프");
//        for(int i=0;i<N;i++) {
//            System.out.println(Arrays.toString(isRudolph[i]));
//        }

        // 게임 진행
        for(int m=0;m<M;m++) {

            if(failSanta==P) break;

            movedRudolph(rudolph,m);
            for(int p=0;p<P;p++) {
                if(!santas[p].fail&&!santas[p].sleep) {
                    movedSanta(santas[p], p, m);
                } else if(!santas[p].fail&&santas[p].sleep&&santas[p].sleepTern+2==m) {
                    movedSanta(santas[p], p,m);
                    santas[p].sleep=false;
                }
            }
            for(int p=0;p<P;p++) {
                if(!santas[p].fail) {
                    santas[p].score+=1;
                }
            }
//            System.out.println("산타" + m);
//            for(int i=0;i<N;i++) {
//                System.out.println(Arrays.toString(isSanta[i]));
//            }
//            System.out.println("루돌프");
//            for(int i=0;i<N;i++) {
//                System.out.println(Arrays.toString(isRudolph[i]));
//            }
        }

        for(int p=0;p<P;p++) {
            output.append(santas[p].score+" ");
        }
        System.out.print(output);

    }

    private static void movedSanta(Santa santa, int santaNum, int tern) {

        PriorityQueue<MoveSanta> pq=new PriorityQueue<>();
        int nowDist=(calDist(santa.x, santa.y, rudolph.x, rudolph.y));
        for(int d=0;d<4;d++) {
            int nx=santa.x+deltas[d][0];
            int ny=santa.y+deltas[d][1];

            if(!isValid(nx,ny)) continue;

            pq.add(new MoveSanta(d, calDist(nx, ny, rudolph.x, rudolph.y), nx,ny));
        }

        MoveSanta selectMovePoint;

        while (!pq.isEmpty()) {
            selectMovePoint=pq.poll();

            // 가까워질 수 있는 방법이 없으면 안 움직임
            if(selectMovePoint.dist>=nowDist) return;

            // 산타 없으면
            if(isSanta[selectMovePoint.nx][selectMovePoint.ny]==-1) {

                // 그 위치로 산타 이동
                isSanta[santa.x][santa.y]=-1; // 산타 위치를 변경시켜주기위해 기존 위치 초기화
                isSanta[selectMovePoint.nx][selectMovePoint.ny]=santaNum;
                santa.x=selectMovePoint.nx;
                santa.y=selectMovePoint.ny;

                // 루돌프있다면
                if(isRudolph[selectMovePoint.nx][selectMovePoint.ny]) {
                    // D 만큼 점수 얻음
                    // 산타는 D칸으로 아동
                    getPoint(santa, (selectMovePoint.direct+2)%4, santa.power, santaNum, tern);
                }
                break;
            }
        }
    }

    private static void movedRudolph(Rudolph rudolph, int tern) {

        PriorityQueue<MoveRudolph> minDistSanta=new PriorityQueue<>();
        PriorityQueue<MoveRudolph> movedMinDistPosition=new PriorityQueue<>();

        // 가장 가까운 산타 찾기
        for(int p=0;p<P;p++) {
            if(santas[p].fail) continue;
            minDistSanta.add(new MoveRudolph(santas[p].x, santas[p].y,calDist(santas[p].x,santas[p].y,rudolph.x,rudolph.y)));
        }

        MoveRudolph selectSanta=minDistSanta.poll();

        // 해당 산타에서 가장 가까운 움직일 위치 찾기
        for(int d=0;d< deltas.length;d++) {
            int nx=rudolph.x+ deltas[d][0];
            int ny=rudolph.y+ deltas[d][1];

            if(!isValid(nx,ny)) continue;

            movedMinDistPosition.add(new MoveRudolph(selectSanta.x, selectSanta.y,calDist(selectSanta.x,selectSanta.y,nx,ny),nx,ny,d));
        }

        MoveRudolph selectMovePoint=movedMinDistPosition.poll();
        isRudolph[rudolph.x][rudolph.y]=false;
        isRudolph[selectMovePoint.nx][selectMovePoint.ny]=true;
        rudolph.x=selectMovePoint.nx;
        rudolph.y=selectMovePoint.ny;

        // 산타가 존재한다면
        if(isSanta[rudolph.x][rudolph.y]!=-1) {
            getPoint(santas[isSanta[rudolph.x][rudolph.y]], selectMovePoint.direct, rudolph.power, isSanta[rudolph.x][rudolph.y], tern);
        }
    }

    private static void getPoint(Santa santa, int direct, int power, int santaNum, int tern) {

        santa.score+=power;
        santa.sleep=true;
        santa.sleepTern=tern;

        int nx=santa.x+(deltas[direct][0]*power);
        int ny=santa.y+(deltas[direct][1]*power);

        isSanta[santa.x][santa.y]=-1; // 산타 위치를 변경시켜주기위해 기존 위치 초기화

        if(!isValid(nx, ny)) {
            santa.fail=true; // 탈락
            failSanta+=1;
        } else {
            // 포물선 궤적으로 이동 후 산타가 존재하면
            if(isSanta[nx][ny]!=-1) {
                if(!santas[isSanta[nx][ny]].fail){
                    // 상호작용
                    interaction(santas[isSanta[nx][ny]], direct, isSanta[nx][ny]);
                }
            }
            isSanta[nx][ny]= santaNum;
            santa.x=nx;
            santa.y=ny;
        }
    }

    private static void interaction(Santa santa, int direct, int santaNum) {

        int nx=santa.x+ deltas[direct][0];
        int ny=santa.y+ deltas[direct][1];

        isSanta[santa.x][santa.y]=-1; // 산타 위치를 변경시켜주기위해 기존 위치 초기화

        if(!isValid(nx, ny)) {
            santa.fail=true; // 탈락
            failSanta+=1;
        } else {
            // 포물선 궤적으로 이동 후 산타가 존재하면
            if(isSanta[nx][ny]!=-1) {
                if(!santas[isSanta[nx][ny]].fail) {
                    // 상호작용
                    interaction(santas[isSanta[nx][ny]], direct, isSanta[nx][ny]);
                }
            }
            isSanta[nx][ny]= santaNum;
            santa.x=nx;
            santa.y=ny;
        }
    }

    private static int calDist(int r1, int c1 , int r2, int c2) {
        return (int) (Math.pow(r1-r2, 2)+ Math.pow(c1-c2, 2));
    }


    private static boolean isValid(int nx, int ny) {
        if(nx<0||nx>=N||ny<0||ny>=N) return false;
        return true;
    }
}