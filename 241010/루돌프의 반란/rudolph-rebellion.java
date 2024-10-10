package Samsung;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer tokens;
	static StringBuilder output=new StringBuilder();
	static int N, M, P, C, D, tern, removedSantaCnt=0;
	static int map[][], deltas[][]={{-1,0},{0,1},{1,0},{0,-1},{-1,-1},{-1,1},{1,-1},{1,1}};
	static Rudolph rudolph;
	static Santa[] santas;
	
	public static class Rudolph {
		int x, y, power, lastDirect;

		public Rudolph(int x, int y, int power) {
			super();
			this.x = x;
			this.y = y;
			this.power = power;
		}
	}
	
	public static class Santa {
		int x, y, power, lastDirect, score=0, stopTern=-2, num;
		boolean isRemoved=false;
		
		public Santa(int x, int y, int power, int num) {
			super();
			this.x = x;
			this.y = y;
			this.num=num;
			this.power = power;
		}
	}
	
	public static class MoveRudolph implements Comparable<MoveRudolph>{
		Santa santa;
		int dist, direct, moveX, moveY;
		
		public MoveRudolph(Santa santa, int direct, int moveX, int moveY) {
			super();
			this.santa = santa;
			this.dist = (moveX-santa.x)*(moveX-santa.x)+(moveY-santa.y)*(moveY-santa.y);
			this.direct = direct;
			this.moveX = moveX;
			this.moveY = moveY;
		}

		@Override
		public int compareTo(MoveRudolph o) {
			if(this.dist==o.dist) {
				if(this.santa.x==o.santa.x) {
					return Integer.compare(this.santa.y, o.santa.y)*-1;
				}
				return Integer.compare(this.santa.x, o.santa.x)*-1;
			}
			return Integer.compare(this.dist, o.dist);
		}		
	}
	
	public static class MoveSanta implements Comparable<MoveSanta> {
		
		int dist, direct, moveX, moveY;

		public MoveSanta(int dist, int direct, int moveX, int moveY) {
			super();
			this.dist = dist;
			this.direct = direct;
			this.moveX = moveX;
			this.moveY = moveY;
		}

		@Override
		public int compareTo(MoveSanta o) {
			
			if(this.dist==o.dist) {
				Integer.compare(this.direct, o.direct);
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
		
		map=new int[N][N];
		santas=new Santa[P+1];
		
		tokens=new StringTokenizer(input.readLine());
		int x=Integer.parseInt(tokens.nextToken())-1;
		int y= Integer.parseInt(tokens.nextToken())-1;
		rudolph=new Rudolph(x, y, C);
		map[x][y]=-1;

		for(int p=1;p<=P;p++) {
			tokens=new StringTokenizer(input.readLine());
			int num=Integer.parseInt(tokens.nextToken());
			x=Integer.parseInt(tokens.nextToken())-1;
			y=Integer.parseInt(tokens.nextToken())-1;
			santas[num]=new Santa(x,y,D,num);
			map[x][y]=num;
		}
		
		for(tern=0;tern<M;tern++) {
			if(removedSantaCnt==P) break;
			
			moveRudolph();
			
//			System.out.println(tern+"턴 루돌프 움직임 ");
//			for(int r=0;r<N;r++) {
//				System.out.println(Arrays.toString(map[r]));
//			}
//			
			moveSanta();
//			
//			System.out.println(tern+"턴 산타 움직임 ");
//			for(int r=0;r<N;r++) {
//				System.out.println(Arrays.toString(map[r]));
//			}
//			
			for(int p=1;p<=P;p++) {
				if(santas[p].isRemoved) continue;
				santas[p].score+=1;
			}
			
//			System.out.println(tern+"턴 ");
//			for(int r=0;r<N;r++) {
//				System.out.println(Arrays.toString(map[r]));
//			}
		}
		
		for(int p=1;p<=P;p++) {
			output.append(santas[p].score+" ");
		}
		
		System.out.println(output);
	}

	private static void moveSanta() {
		
		loop : for(int p=1;p<=P;p++) {
			
			PriorityQueue<MoveSanta> pq=new PriorityQueue<>();
			
			// 기절하거나 제거된 산타는 제외 
			if(santas[p].isRemoved||santas[p].stopTern+2>tern) continue;
		
			for(int d=0;d<4;d++) {
				int nx=santas[p].x+deltas[d][0];
				int ny=santas[p].y+deltas[d][1];
				
				if(nx<0||ny>=N||ny<0||ny>=N) continue;
				
				int dist=(rudolph.x-nx)*(rudolph.x-nx)+(rudolph.y-ny)*(rudolph.y-ny);
				pq.add(new MoveSanta(dist, d, nx, ny));
			}
			
			MoveSanta moveSanta=pq.poll();
			int originDist=(rudolph.x-santas[p].x)*(rudolph.x-santas[p].x)+(rudolph.y-santas[p].y)*(rudolph.y-santas[p].y);
			while(true) {
				// 산타가 더 이상 움직일 수 있는 위치가 없음 
				if(pq.size()==0) continue loop;
				// 산타가 이동하는 위치에 다른 산타가 있으면
				if(map[moveSanta.moveX][moveSanta.moveY]>0) {
					// 가까워질 수 있다면 다음 이동 위치가 존재하면 갱신 
					if(originDist>pq.peek().dist) {
						moveSanta=pq.poll();
					} 
					// 최단 거리가 없으므로 산타는 움직이지 않음 
					else continue loop;
				} else break;
			}
			
			// 이동 위치에 루돌프 있으면 
			if(map[moveSanta.moveX][moveSanta.moveY]==-1) {
				// 점수
				santas[p].score+=santas[p].power;
				santas[p].stopTern=tern;
				// 산타 반대 편으로 이동 
				interact(santas[p], (moveSanta.direct+2)%4, santas[p].power-1);
				
			} else {
				// 단순 이동
				map[santas[p].x][santas[p].y]=0;
				santas[p].x=moveSanta.moveX;
				santas[p].y=moveSanta.moveY;
				// map에 산타 이동 표시
				map[santas[p].x][santas[p].y]=santas[p].num;
			}	
		}
	}

	private static void moveRudolph() {
		PriorityQueue<MoveRudolph> pq=new PriorityQueue<>();
		
		for(int d=0;d<deltas.length;d++) {
			for(int p=1;p<=P;p++) {
				if(santas[p].isRemoved) continue; // 탈락한 산타는 제외				
				
				int nx=rudolph.x+deltas[d][0];
				int ny=rudolph.y+deltas[d][1];
				if(nx<0||ny>=N||ny<0||ny>=N) continue;
				
				pq.add(new MoveRudolph(santas[p], d, nx, ny));
			}
		}
		
		MoveRudolph moveRudolph=pq.poll();
		
		// 루돌프 이동
		map[rudolph.x][rudolph.y]=0;
		rudolph.x=moveRudolph.moveX;
		rudolph.y=moveRudolph.moveY;
		
		// 이동한 위치에 산타있을 경우
		if(map[rudolph.x][rudolph.y]>0) {
			// 점수얻기
			santas[map[rudolph.x][rudolph.y]].score+=rudolph.power;
			// 기절한 턴 입력 
			santas[map[rudolph.x][rudolph.y]].stopTern=tern;
			// 산타 이동 
			interact(santas[map[rudolph.x][rudolph.y]], moveRudolph.direct, rudolph.power);
		}
		// map에 루돌프 이동 표시 
		map[rudolph.x][rudolph.y]=-1;
	}

	private static void interact(Santa santa, int direct, int power) {
		
		int nx=santa.x+deltas[direct][0]*power;
		int ny=santa.y+deltas[direct][1]*power;
		
		// 밖으로 나가면 
		if(nx<0||nx>=N||ny<0||ny>=N) {
			santa.isRemoved=true;
			map[santa.x][santa.y]=0;
			removedSantaCnt+=1;
			return;
		}
		
		// 이동
		map[santa.x][santa.y]=0;
		santa.x=nx;
		santa.y=ny;
		
		// 이동한 곳에 산타가 있으면 상호작용 
		if(map[santa.x][santa.y]>0) {
			interact(santas[map[santa.x][santa.y]], direct, 1);
		}
 		map[santa.x][santa.y]=santa.num;
	}

}