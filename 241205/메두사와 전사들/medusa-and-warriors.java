import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer tokens;
	static StringBuilder output=new StringBuilder();
	static int N, M, totalDist, stopCnt, attackCnt;
	static int[][] map, deltas= {{-1,0},{1,0},{0,-1},{0,1}};
	static boolean[][] light, temp;
	static int[] start, finish;
	static Warrior[] warriors;
	static List<int[]> route;
	
	public static class Route {
		int x,y;
		List<int[]> route=new LinkedList<>();
		
		public Route(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		public Route(int x, int y, List<int[]> route) {
			super();
			this.x = x;
			this.y = y;
			this.route.addAll(route);
			this.route.add(new int[] {x,y});
		}
		
	}
	
	public static class Warrior {
		int x, y;
		boolean removed, isStop;
		
		public Warrior(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		
	}
	public static void main(String[] args) throws IOException {
		tokens=new StringTokenizer(input.readLine());
		N=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		
		start=new int[2];
		finish=new int[2];
		map=new int[N][N];
		light=new boolean[N][N];
		temp=new boolean[N][N];
		warriors=new Warrior[M];
		route=new ArrayList<>();
		
		tokens=new StringTokenizer(input.readLine());
		start[0]=Integer.parseInt(tokens.nextToken());
		start[1]=Integer.parseInt(tokens.nextToken());
		finish[0]=Integer.parseInt(tokens.nextToken());
		finish[1]=Integer.parseInt(tokens.nextToken());
		
		tokens=new StringTokenizer(input.readLine());
		for(int m=0;m<M;m++) {
			int x=Integer.parseInt(tokens.nextToken());
			int y=Integer.parseInt(tokens.nextToken());
			warriors[m]=new Warrior(x, y);
		}
		
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) map[r][c]=Integer.parseInt(tokens.nextToken());
		}
		
		if(!moveRoute()) {
			System.out.println(-1);
			return;
		}
		
		for(int[] next:route) {
			// 메두사 이동 
			start[0]=next[0];
			start[1]=next[1];
			
			// 메두사가 움직인 위치에 전사가 있을경우 제거
			for(Warrior warrior:warriors) {
				if(warrior.x==start[0]&&warrior.y==start[1]) {
					warrior.removed=true;
				}
			}
			
			// 초기화 
			totalDist=0;
			stopCnt=0;
			attackCnt=0;
			
			selectDir();
			
//			for(int r=0;r<N;r++) System.out.println(Arrays.toString(light[r]));
//			System.out.println();
			
			warriorStop();
			
			moveWarrior();
			
			output.append(totalDist+" "+stopCnt+" "+attackCnt+"\n");
		}
		output.append(0);
		System.out.println(output);
	}
	private static void warriorStop() {
		for(Warrior warrior:warriors) {
			if(warrior.removed) continue;
			 warrior.isStop=false; // 그 전 결과는 초기화 
			if(light[warrior.x][warrior.y]) {
				warrior.isStop=true; // 현재 돌이 되었는지 셋팅 
				stopCnt+=1; // 돌이 된 전사 수 구하기 
			}
		}
	}
	private static void moveWarrior() {
		int[][] deltas2= {{0,-1},{0,1},{-1,0},{1,0}};
		for(Warrior warrior:warriors) {
			// 돌로 변하지 않은 전사들만 움직임 
			if(warrior.removed||warrior.isStop) continue;
			
			// 첫번째 이동 
			int dist=Math.abs(warrior.x-start[0])+Math.abs(warrior.y-start[1]);
			int dir=-1;
			
			for(int d=0;d<4;d++) {
				int nx=warrior.x+deltas[d][0];
				int ny=warrior.y+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				if(light[nx][ny]) continue;
				
				int nextDist=Math.abs(nx-start[0])+Math.abs(ny-start[1]);
				
				if(dist>nextDist) {
					dist=nextDist;
					dir=d;
				}
			}
			if(dir==-1) continue;
			totalDist+=1;
			warrior.x+=deltas[dir][0];
			warrior.y+=deltas[dir][1];
			
			// 전사가 움직인 위치에 메두사가 존재하면 공격한 전사수 +1, 제거 
			if(warrior.x==start[0]&&warrior.y==start[1]) {
				attackCnt+=1;
				warrior.removed=true;
				continue;
			}
			
			// 두번째 이동 
			dist=Math.abs(warrior.x-start[0])+Math.abs(warrior.y-start[1]);
			dir=-1;
			
			for(int d=0;d<4;d++) {
				int nx=warrior.x+deltas2[d][0];
				int ny=warrior.y+deltas2[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				if(light[nx][ny]) continue;
				
				int nextDist=Math.abs(nx-start[0])+Math.abs(ny-start[1]);
				
				if(dist>nextDist) {
					dist=nextDist;
					dir=d;
				}
			}
			if(dir==-1) continue;
			totalDist+=1;
			warrior.x+=deltas2[dir][0];
			warrior.y+=deltas2[dir][1];	
			
			// 전사가 움직인 위치에 메두사가 존재하면 공격한 전사수 +1, 제거 
			if(warrior.x==start[0]&&warrior.y==start[1]) {
				attackCnt+=1;
				warrior.removed=true;
			}
		}
	}
	private static void selectDir() {
		int maxCnt=Integer.MIN_VALUE;
		int cnt=Integer.MIN_VALUE;
		
		cnt=up();
		if(maxCnt<cnt) {
			maxCnt=cnt;
			setLight();
		}
		
		cnt=down();
		if(maxCnt<cnt) {
			maxCnt=cnt;
			setLight();
		}
		
		cnt=left();
		if(maxCnt<cnt) {
			maxCnt=cnt;
			setLight();
		}
		
		cnt=right();
		if(maxCnt<cnt) {
			maxCnt=cnt;
			setLight();
		}
	}
	private static void setLight() {
		for(int r=0;r<N;r++) {
			for(int c=0;c<N;c++) {
				light[r][c]=temp[r][c];
			}
		}
	}
	private static int right() {
		temp=new boolean[N][N];
		int x=start[0]+deltas[3][0];
		int y=start[1]+deltas[3][1];
		int size=3;
		
		// 메두사 시야 확보 
		while(y<N) {
			int startX=Math.max(0, x-size/2);
			int endX=Math.min(N,startX+size);
			
			for(int nx=startX;nx<endX;nx++) temp[nx][y]=true;
			x+=deltas[3][0];
			y+=deltas[3][1];
			size+=2;
		}
		
		// 전사가 시야에 가려졌는지 확인 
		// 가려졌다면 그 뒤 시야는 false로 변환 
		for(Warrior warrior:warriors) {
			if(warrior.removed||!temp[warrior.x][warrior.y]) continue;
			
			x=warrior.x+deltas[3][0];
			y=warrior.y+deltas[3][1];
			size=2;
			
			if(warrior.x<start[0]) {	
				while(y<N) {
					int startX=Math.max(0, x-size);
					
					for(int nx=startX;nx<warrior.x;nx++) temp[nx][y]=false;
					x+=deltas[3][0];
					y+=deltas[3][1];
					size+=1;
				}
			} else if(warrior.x>start[0]) {
				while(y<N) {
					int endX=Math.min(N,warrior.x+size);
					
					for(int nx=warrior.x;nx<endX;nx++) temp[nx][y]=false;
					x+=deltas[3][0];
					y+=deltas[3][1];
					size+=1;
				}
			} else {
				while(y<N) {
					temp[x][y]=false;
					x+=deltas[3][0];
					y+=deltas[3][1];
				}
			}
		}
		
		int cnt=0;
		for(Warrior warrior:warriors) {
			if(warrior.removed) continue;
			if(temp[warrior.x][warrior.y]) cnt+=1;
		}
		return cnt;
	}
	private static int left() {
		temp=new boolean[N][N];
		int x=start[0]+deltas[2][0];
		int y=start[1]+deltas[2][1];
		int size=3;
		
		// 메두사 시야 확보 
		while(y>=0) {
			int startX=Math.max(0, x-size/2);
			int endX=Math.min(N,startX+size);
			
			for(int nx=startX;nx<endX;nx++) temp[nx][y]=true;
			x+=deltas[2][0];
			y+=deltas[2][1];
			size+=2;
		}
		
		// 전사가 시야에 가려졌는지 확인 
		// 가려졌다면 그 뒤 시야는 false로 변환 
		for(Warrior warrior:warriors) {
			if(warrior.removed||!temp[warrior.x][warrior.y]) continue;
			
			x=warrior.x+deltas[2][0];
			y=warrior.y+deltas[2][1];
			size=2;
			
			if(warrior.x<start[0]) {	
				while(y>=0) {
					int startX=Math.max(0, x-size);
					
					for(int nx=startX;nx<warrior.x;nx++) temp[nx][y]=false;
					x+=deltas[2][0];
					y+=deltas[2][1];
					size+=1;
				}
			} else if(warrior.x>start[0]) {
				while(y>=0) {
					int endX=Math.min(N,warrior.x+size);
					
					for(int nx=warrior.x;nx<endX;nx++) temp[nx][y]=false;
					x+=deltas[2][0];
					y+=deltas[2][1];
					size+=1;
				}
			} else {
				while(y>=0) {
					temp[x][y]=false;
					x+=deltas[2][0];
					y+=deltas[2][1];
				}
			}
		}
		
		int cnt=0;
		for(Warrior warrior:warriors) {
			if(warrior.removed) continue;
			if(temp[warrior.x][warrior.y]) cnt+=1;
		}
		return cnt;
	}
	private static int down() {
		temp=new boolean[N][N];
		int x=start[0]+deltas[1][0];
		int y=start[1]+deltas[1][1];
		int size=3;
		
		// 메두사 시야 확보 
		while(x<N) {
			int startY=Math.max(0, y-size/2);
			int endY=Math.min(N,startY+size);
			
			for(int ny=startY;ny<endY;ny++) temp[x][ny]=true;
			x+=deltas[1][0];
			y+=deltas[1][1];
			size+=2;
		}
		
		// 전사가 시야에 가려졌는지 확인 
		// 가려졌다면 그 뒤 시야는 false로 변환 
		for(Warrior warrior:warriors) {
			if(warrior.removed||!temp[warrior.x][warrior.y]) continue;
			
			x=warrior.x+deltas[1][0];
			y=warrior.y+deltas[1][1];
			size=2;
			
			if(warrior.y<start[1]) {	
				while(x<N) {
					int startY=Math.max(0, y-size);
					
					for(int ny=startY;ny<warrior.y;ny++) temp[x][ny]=false;
					x+=deltas[1][0];
					y+=deltas[1][1];
					size+=1;
				}
			} else if(warrior.y>start[1]) {
				while(x<N) {
					int endY=Math.min(N,warrior.y+size);
					for(int ny=warrior.y;ny<endY;ny++) temp[x][ny]=false;
					x+=deltas[1][0];
					y+=deltas[1][1];
					size+=1;
				}
			} else {
				while(x<N) {
					temp[x][y]=false;
					x+=deltas[1][0];
					y+=deltas[1][1];
				}
			}
		}
		
		int cnt=0;
		for(Warrior warrior:warriors) {
			if(warrior.removed) continue;
			if(temp[warrior.x][warrior.y]) cnt+=1;
		}
		return cnt;
	}
	private static int up() {
		temp=new boolean[N][N];
		int x=start[0]+deltas[0][0];
		int y=start[1]+deltas[0][1];
		int size=3;
		
		// 메두사 시야 확보 
		while(x>=0) {
			int startY=Math.max(0, y-size/2);
			int endY=Math.min(N,startY+size);
			
			for(int ny=startY;ny<endY;ny++) temp[x][ny]=true;
			x+=deltas[0][0];
			y+=deltas[0][1];
			size+=2;
		}
		
		// 전사가 시야에 가려졌는지 확인 
		// 가려졌다면 그 뒤 시야는 false로 변환 
		for(Warrior warrior:warriors) {
			if(warrior.removed||!temp[warrior.x][warrior.y]) continue;
			
			x=warrior.x+deltas[0][0];
			y=warrior.y+deltas[0][1];
			size=2;
			
			if(warrior.y<start[1]) {	
				while(x>=0) {
					int startY=Math.max(0, y-size);
					
					for(int ny=startY;ny<warrior.y;ny++) temp[x][ny]=false;
					x+=deltas[0][0];
					y+=deltas[0][1];
					size+=1;
				}
			} else if(warrior.y>start[1]) {
				while(x>=0) {
					
					int endY=Math.min(N,warrior.y+size);
					for(int ny=warrior.y;ny<endY;ny++) temp[x][ny]=false;
					x+=deltas[0][0];
					y+=deltas[0][1];
					size+=1;
				}
			} else {
				while(x>=0) {
					temp[x][y]=false;
					x+=deltas[0][0];
					y+=deltas[0][1];
				}
			}
		}
		
		int cnt=0;
		for(Warrior warrior:warriors) {
			if(warrior.removed) continue;
			if(temp[warrior.x][warrior.y]) cnt+=1;
		}
		return cnt;
	}
	private static boolean moveRoute() {
		Queue<Route> q=new LinkedList<>();
		boolean visited[][]=new boolean[N][N];
		q.add(new Route(start[0],start[1]));
		visited[start[0]][start[1]]=true;
		
		while(q.size()>0) {
			Route now=q.poll();
				
			for(int d=0;d<4;d++) {
				int nx=now.x+deltas[d][0];
				int ny=now.y+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				
				if(nx==finish[0]&&ny==finish[1]) {
					route=now.route;
					return true;
				}
				
				else if(map[nx][ny]==0&&!visited[nx][ny]) {
					q.add(new Route(nx,ny,now.route));
					visited[nx][ny]=true;
				} 
			}
		}
		return false;
	}

}
