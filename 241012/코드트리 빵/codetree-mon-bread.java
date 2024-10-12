import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer tokens;
	static int N, M, time;
	static boolean[][] disable;
	static List<int[]> basecamps;
	static List<People> people;
	static Queue<People> movedPeople;
	static int[][] deltas = {{-1,0},{0,-1},{0,1},{1,0}}, map;
	
	public static class People{
		int x, y, wantPlaceX, wantPlaceY;

		public People(int x, int y, int wantPlaceX, int wantPlaceY) {
			super();
			this.x = x;
			this.y = y;
			this.wantPlaceX = wantPlaceX;
			this.wantPlaceY = wantPlaceY;
		}
	}
	public static class Position implements Comparable<Position>{
		int x, y, cnt;

		public Position(int x, int y, int cnt) {
			super();
			this.x = x;
			this.y = y;
			this.cnt = cnt;
		}

		@Override
		public int compareTo(Position o) {
			if(this.cnt==o.cnt) {
				if(this.x==o.x) {
					return Integer.compare(this.y, o.y);
				}
				return  Integer.compare(this.x, o.x);
			}
			return  Integer.compare(this.cnt, o.cnt);
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		tokens = new StringTokenizer(input.readLine());
		N = Integer.parseInt(tokens.nextToken());
		M = Integer.parseInt(tokens.nextToken());
		
		disable = new boolean[N][N];
		map = new int[N][N];
		basecamps = new ArrayList<>();
		people = new ArrayList<>();
		movedPeople = new LinkedList<>();
		time = 0;
		
		for(int r=0;r<N;r++) {
			tokens =  new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				map[r][c] = Integer.parseInt(tokens.nextToken());
				if(map[r][c]==1) basecamps.add(new int[] {r,c});
			}
		}
		
		for(int m=0; m<M; m++) {
			tokens =  new StringTokenizer(input.readLine());
			int x =  Integer.parseInt(tokens.nextToken())-1;
			int y =  Integer.parseInt(tokens.nextToken())-1;
			people.add(new People(0,0,x,y));
		}
				
		while(true) {
			time++;
			movedFristRule();
			movedThirdRule();
			
			if(movedPeople.isEmpty()) break;
		}
		System.out.println(time);
	}

	private static void movedThirdRule() {
		if(time<= M) {
			People now = people.get(time-1);
			
			// 출발할 베이스 캠프 찾기 
			int[] destBasecamp = findBasecamp(now);
			
			// 이동
			now.x=destBasecamp[0];
			now.y=destBasecamp[1];
			movedPeople.add(now);
			
			// 베이스캠프 못 지나감
			disable[destBasecamp[0]][destBasecamp[1]] = true;
		}
	}

	private static int[] findBasecamp(People people) {
		PriorityQueue<Position> pq = new PriorityQueue<>();
		boolean[][] visited = new boolean[N][N];
		pq.add(new Position(people.wantPlaceX, people.wantPlaceY,0));
		visited[people.wantPlaceX][people.wantPlaceY]=true;
		
		while(pq.size()>0) {
			Position now = pq.poll();
			
			if(map[now.x][now.y]==1&&!disable[now.x][now.y]) {
				return new int[] {now.x,now.y};
			}
			
			for(int d=0;d<deltas.length;d++) {
				int nx = now.x + deltas[d][0];
				int ny = now.y + deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				
				if(!disable[nx][ny]&&!visited[nx][ny]) {
					visited[nx][ny]=true;
					pq.add(new Position(nx,ny,now.cnt+1));
				}
			}
		}
		
		return null;
	}

	private static void movedFristRule() {
		int cnt = movedPeople.size();
		for(int i=0;i<cnt;i++) {
			People now = movedPeople.poll();
			
			// 최단 거리 방향 찾기 
			int dir = findDist(now);
//			int dir = -1;
//			int minDist = Integer.MAX_VALUE;
//			for(int d=0;d<deltas.length;d++) {
//				int nx = now.x+deltas[d][0];
//				int ny = now.x+deltas[d][1];
//				if(!disable[nx][ny]) {
//					int dist = findDist(now, d);
//					if(dist!=-1&&dist<minDist) {
//						dir = d;
//						minDist = dist;
//					}
//				}
//			}
			
			// 움직임 
			if(dir!=-1) {
				now.x = now.x+deltas[dir][0];
				now.y = now.y+deltas[dir][1];
			}
			
			// 편의점 도착 여부  
			if(now.x==now.wantPlaceX&&now.y==now.wantPlaceY) { // 도착 
				disable[now.wantPlaceX][now.wantPlaceY] = true;
 			} else { //도착 X 
 				movedPeople.add(now);
 			}
		}
	}

//	private static int findDist(People people, int dir) {
//		
//		Queue<int[]> q = new LinkedList<>();
//		boolean[][] visited = new boolean[N][N];
//		q.add(new int[] {people.x+deltas[dir][0], people.y+deltas[dir][1],1});
//		visited[people.x+deltas[dir][0]][people.y+deltas[dir][1]]=true;
//		while(q.size()>0) {
//			int[] now = q.poll();
//			if(people.wantPlaceX==now[0]&&people.wantPlaceY==now[1]) {
//				return now[2];
//			}
//			
//			for(int d=0;d<deltas.length;d++) {
//				int nx = now[0] + deltas[d][0];
//				int ny = now[1] + deltas[d][1];
//				
//				if(nx<0||nx>=N||ny<0||ny>=N) continue;
//				
//				if(!disable[nx][ny]&&!visited[nx][ny]) {
//					visited[nx][ny]=true;
//					q.add(new int[] {nx,ny,now[2]+1});
//				}
//			}
//		}
//		return -1;
//	}
	private static int findDist(People people) {
		Queue<int[]> q = new LinkedList<>();
		boolean[][] visited = new boolean[N][N];
		q.add(new int[] {people.x, people.y, 0, 0}); // x,y,cny,dir
		visited[people.x][people.y]=true;
		
		while(q.size()>0) {
			int[] now = q.poll();
			if(people.wantPlaceX==now[0]&&people.wantPlaceY==now[1]) {
				return now[3];
			}
			
			for (int d = 0; d < deltas.length; d++) {
				int nx = now[0] + deltas[d][0];
				int ny = now[1] + deltas[d][1];

				if (nx < 0 || nx >= N || ny < 0 || ny >= N)
					continue;

				if (!disable[nx][ny] && !visited[nx][ny]) {
					visited[nx][ny] = true;
					if(now[2]==0) q.add(new int[] { nx, ny, now[2] + 1, d});
					else q.add(new int[] { nx, ny, now[2] + 1, now[3]});
				}
			}
			
		}
		
		return -1;
	}

}