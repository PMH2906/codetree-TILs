import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int N, M, P, C, D, removedSanta, tern;
	static int[][] map, deltas= {{-1,0},{0,1},{1,0},{0,-1},{-1,-1},{-1,1},{1,-1},{1,1}};
	static Roudolph roudolph;
	static Santa[] santas;
	
	public static class Santa {
		int x, y, score, stopTern, dir, num;
		boolean isRemoved;
		
		public Santa(int x, int y, int score, int stopTern, boolean isRemoved, int num) {
			super();
			this.x = x;
			this.y = y;
			this.score = score;
			this.stopTern = stopTern;
			this.isRemoved = isRemoved;
			this.num=num;
		}
	}
	
	public static class Roudolph {
		int x, y, dir;

		public Roudolph(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
	}
	
	public static class Info implements Comparable<Info> {
		Santa santa;
		int dist;
		
		public Info(Santa santa) {
			super();
			this.santa = santa;
			this.dist = (santa.x-roudolph.x)*(santa.x-roudolph.x)+(santa.y-roudolph.y)*(santa.y-roudolph.y);
		}

		// 만약 가장 가까운 산타가 2명 이상이라면, r 좌표가 큰 산타를 향해 돌진합니다. r이 동일한 경우, c 좌표가 큰 산타를 향해 돌진합니다.
		@Override
		public int compareTo(Info o) {
			if(this.dist==o.dist) {
				if(this.santa.x==o.santa.x) {
					return Integer.compare(this.santa.y,o.santa.y)*-1;
				}
				return Integer.compare(this.santa.x,o.santa.x)*-1;
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
		
		santas=new Santa[P+1];
		removedSanta=P;
		map=new int[N][N];
				
		tokens=new StringTokenizer(input.readLine());
		int x=Integer.parseInt(tokens.nextToken())-1;
		int y=Integer.parseInt(tokens.nextToken())-1;
		
		roudolph=new Roudolph(x,y);
		map[x][y]=-1;
		
		for(int p=0;p<P;p++) {
			tokens=new StringTokenizer(input.readLine());
			int num=Integer.parseInt(tokens.nextToken());
			x=Integer.parseInt(tokens.nextToken())-1;
			y=Integer.parseInt(tokens.nextToken())-1;
			santas[num]=new Santa(x, y, 0, 0, false,num);
			map[x][y]=num;
		}
		
		for(tern=1;tern<=M;tern++) {
			
			// 루돌프 움직임
			moveRoudolph();
			
//			for(int r=0;r<N;r++) System.out.println(Arrays.toString(map[r]));
//			System.out.println();
			
			// 산타가 모두 게임에서 탈락하게 된다면 그 즉시 게임이 종료
			if(removedSanta==0) break;
			
			// 산타 움직임
			moveSanta();
			
//			for(int r=0;r<N;r++) System.out.println(Arrays.toString(map[r]));
//			System.out.println();
			
			// 산타가 모두 게임에서 탈락하게 된다면 그 즉시 게임이 종료
			if(removedSanta==0) break;
			
			for(int p=1;p<=P;p++) {
				if(santas[p].isRemoved) continue;
				santas[p].score+=1;
			}
		}
		
		for(int p=1;p<=P;p++) {
			output.append(santas[p].score+" ");
		}
		System.out.println(output);
	}

	private static void moveSanta() {
		
		for(int p=1;p<=P;p++) {
			
			// 기절, 탈락한 산타는 움직이지 X 
			if(santas[p].isRemoved||santas[p].stopTern>=tern) continue;
			
			int minDist=(santas[p].x-roudolph.x)*(santas[p].x-roudolph.x)+(santas[p].y-roudolph.y)*(santas[p].y-roudolph.y);
			int dir=-1;
			
			for(int d=0;d<4;d++) {
				int nx=santas[p].x+deltas[d][0];
				int ny=santas[p].y+deltas[d][1];
				
				// 산타는 루돌프에게 거리가 가장 가까워지는 방향으로 1칸 이동합니다.
				// 산타는 다른 산타가 있는 칸이나 게임판 밖으로는 움직일 수 없습니다.
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				if(map[nx][ny]>0) continue;
				
				int dist=(nx-roudolph.x)*(nx-roudolph.x)+(ny-roudolph.y)*(ny-roudolph.y);
				
				if(dist<minDist) {
					minDist=dist;
					dir=d;
				}
			}
			
			//움직일 수 있는 칸이 있더라도 만약 루돌프로부터 가까워질 수 있는 방법이 없다면 산타는 움직이지 않습니다.
			if(dir==-1) continue;
			
			map[santas[p].x][santas[p].y]=0;
			santas[p].x+=deltas[dir][0];
			santas[p].y+=deltas[dir][1];
			santas[p].dir=dir;
		
			// System.out.println(dir+" "+santas[p].x+" "+santas[p].y);
			// 루돌프가 존재하면 충돌 
			if(map[santas[p].x][santas[p].y]<0) {
				attack(santas[p], (dir+2)%4, D);
			}
			else map[santas[p].x][santas[p].y]=santas[p].num;
		}
		
	}

	private static void moveRoudolph() {
		PriorityQueue<Info> pq=new PriorityQueue<>();
		
		for(int p=1;p<=P;p++) {
			if(santas[p].isRemoved) continue;
			pq.add(new Info(santas[p]));
		}
		
		Info selectSanta=pq.poll();
		Santa santa=selectSanta.santa;
		int minDist=Integer.MAX_VALUE;
		int dir=-1;
		
		for(int d=0;d<8;d++) {
			int nx=roudolph.x+deltas[d][0];
			int ny=roudolph.y+deltas[d][1];
			
			if(nx<0||nx>=N||ny<0||ny>=N) continue;
			
			int dist=(santa.x-nx)*(santa.x-nx)+(santa.y-ny)*(santa.y-ny);
			
			if(dist<minDist) {
				minDist=dist;
				dir=d;
			}
		}
		
		map[roudolph.x][roudolph.y]=0;
		roudolph.x+=deltas[dir][0];
		roudolph.y+=deltas[dir][1];
		roudolph.dir=dir;
		
		// 산타 존재하면 충돌 
		if(map[roudolph.x][roudolph.y]>0) {
			attack(santas[map[roudolph.x][roudolph.y]], dir, C);
		}
		map[roudolph.x][roudolph.y]=-1;
	}

	private static void attack(Santa santa, int dir, int score) {
		// map[santa.x][santa.y]=0;
		santa.x+=deltas[dir][0]*score;
		santa.y+=deltas[dir][1]*score;
		santa.score+=score;
		santa.dir=dir;
		santa.stopTern=tern+1;
		
		// 산타 제거 
		if(santa.x<0||santa.x>=N||santa.y<0||santa.y>=N) {
			santa.isRemoved=true;
			removedSanta-=1;
			return;
		}
		
		// 산타 존재하면 충돌 
		if(map[santa.x][santa.y]>0) {
			// 상호작용 
			effect(santas[map[santa.x][santa.y]], santa.dir);
		}
		map[santa.x][santa.y]=santa.num;
	}

	private static void effect(Santa santa, int dir) {
		map[santa.x][santa.y]=0;
		santa.x+=deltas[dir][0];
		santa.y+=deltas[dir][1];
		santa.dir=dir;
		
		// 산타 제거 
		if(santa.x<0||santa.x>=N||santa.y<0||santa.y>=N) {
			santa.isRemoved=true;
			removedSanta-=1;
			return;
		}
		
		// 산타 존재하면 충돌 
		if(map[santa.x][santa.y]>0) {
			// 상호작용 
			effect(santas[map[santa.x][santa.y]], santa.dir);
		}
		map[santa.x][santa.y]=santa.num;
	}
}
