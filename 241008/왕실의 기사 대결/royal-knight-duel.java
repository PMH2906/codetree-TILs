import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.tools.DiagnosticCollector;

public class Main {
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int L, N, Q, ans;
	static int map[][], mapDriver[][], newMapDriver[][], deltas[][]={{-1,0},{0,1},{1,0},{0,-1}};
	static Driver[] drivers;
	
	public static class Point {
		int x, y;

		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
	}
	
	public static class Driver {
		int k, damage;
		boolean removed;
		List<Point> points=new ArrayList<>();
		List<Point> newPoints=new ArrayList<>();
		
		
		public Driver(int k, boolean removed) {
			super();
			this.k = k;
			this.removed = removed;
			this.damage=0;
		}
	}
	
	public static void main(String[] args) throws IOException {
		tokens=new StringTokenizer(input.readLine());
		
		L=Integer.parseInt(tokens.nextToken());
		N=Integer.parseInt(tokens.nextToken());
		Q=Integer.parseInt(tokens.nextToken());
		map=new int[L][L];
		mapDriver=new int[L][L];
		drivers=new Driver[N+1];
		
		for(int r=0;r<L;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<L;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		
		for(int n=1;n<=N;n++) {
			tokens=new StringTokenizer(input.readLine());
			int x=Integer.parseInt(tokens.nextToken())-1;
			int y=Integer.parseInt(tokens.nextToken())-1;
			int h=Integer.parseInt(tokens.nextToken());
			int w=Integer.parseInt(tokens.nextToken());
			int k=Integer.parseInt(tokens.nextToken());
			
			drivers[n]=new Driver(k, false);
			
			for(int r=0;r<h;r++) {
				for(int c=0;c<w;c++) {
					drivers[n].points.add(new Point(x+r, y+c));
					mapDriver[x+r][y+c]=n;
				}
			}
		}
		for(int q=0;q<Q;q++) {
			tokens=new StringTokenizer(input.readLine());
			int n=Integer.parseInt(tokens.nextToken());
			int dir=Integer.parseInt(tokens.nextToken());
			
			if(!drivers[n].removed) {
				order(n, dir);
			}
			
//			System.out.println(q);
//			for(int r=0;r<L;r++) {
//				System.out.println(Arrays.toString(mapDriver[r]));
//			}
		}
		
		for(int n=1;n<=N;n++) {
			if(!drivers[n].removed) ans+=drivers[n].damage;
		}
		System.out.println(ans);
	}

	private static void damage(int num) {
	
		// 움직인 기사의 체력 감소 
		for(int n=1;n<=N;n++) {
			if(n==num) continue;
			for(Point point:drivers[n].newPoints) {
				if(map[point.x][point.y]==1) {
					drivers[n].k-=1;
					drivers[n].damage+=1;
					if(drivers[n].k<=0) drivers[n].removed=true;
				}
			}
		}
	}

	private static void order(int num, int dir) {
		
		// 새로운 맵 초기화 
		newMapDriver=new int[L][L];
		
		
		// 새로운 기사 위치 초기화 
		for(int n=1;n<=N;n++) {
			drivers[n].newPoints=new ArrayList<>();
		}
				
		// 움직일 수 없는 경우 
		if(!move(num,dir,new boolean[L][L])) return;

		damage(num);
		
		// map 갱신 
		mapDriver=new int[L][L];
		for(int n=1;n<=N;n++) {
			// 제거된 기사는 map에서 제외 
			if(drivers[n].removed) continue;
			
			if(drivers[n].newPoints.size()==0) {
				for(Point point : drivers[n].points) {
					mapDriver[point.x][point.y]=n;
				}
			} else {
				for(Point point : drivers[n].newPoints) {
					mapDriver[point.x][point.y]=n;
					drivers[n].points=new ArrayList<>(drivers[n].newPoints);
				}
			}
			
		}
		
//		System.out.println("이동 " + dir);
//		for(int r=0;r<L;r++) {
//			System.out.println(Arrays.toString(mapDriver[r]));
//		}	
	}

	private static boolean move(int n, int dir, boolean[][] visited) {
		
		for(Point point : drivers[n].points) {
			visited[point.x][point.y]=true;
			
			// 이동 
			int nx=point.x+deltas[dir][0];
			int ny=point.y+deltas[dir][1];
		
			if(nx<0||nx>=L||ny<0||ny>=L||map[nx][ny]==2) return false;
			
			if(n!=mapDriver[nx][ny]&&0!=mapDriver[nx][ny]&&!visited[nx][ny]) {
				if(!move(mapDriver[nx][ny],dir,visited)) {
					return false;
				}
			}
			
			newMapDriver[nx][ny]=n;
			// 새롭게 이동한 위치 
			drivers[n].newPoints.add(new Point(nx,ny));
		}
		return true;
		
	}
}