import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 주의
 * 1. 움직이지않은 기사의 데미지는 감소하지 않는다. 
 * **/
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
		
		// 명령 실행 
		for(int q=0;q<Q;q++) {
			tokens=new StringTokenizer(input.readLine());
			int n=Integer.parseInt(tokens.nextToken());
			int dir=Integer.parseInt(tokens.nextToken());
			
			// 제거되지않은 기사만 명령 실행 
			if(!drivers[n].removed) {
				order(n, dir);
			}
		}
		
		for(int n=1;n<=N;n++) {
			if(!drivers[n].removed) ans+=drivers[n].damage;
		}
		System.out.println(ans);
	}

	private static void damage(int num) {
	
		// 움직인 기사의 체력 감소 
		for(int n=1;n<=N;n++) {
			// 명령 실행한 기사는 데미지 받기 제외 
			if(n==num) continue;
			
			// 현재 명령에 움직인 기사만 데미지 받기 
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
				
		// 움직일 수 없는 경우 명령 종료 
		if(!move(num,dir,new boolean[L][L])) return;

		// 데미지 계산 
		damage(num);
		
		// 새로운 map 갱신 
		mapDriver=new int[L][L];
		for(int n=1;n<=N;n++) {
			// 제거된 기사는 map에서 제외 
			if(drivers[n].removed) continue;
			
			// 현재 명령에 움직이지 않은 기사는 기존의 위치로 map 갱신 
			if(drivers[n].newPoints.size()==0) {
				for(Point point : drivers[n].points) {
					mapDriver[point.x][point.y]=n;
				}
			} 
			// 현재 명령에 움진인 기사는 움직인 위치로 map 갱신
			// 기사의 위치도 갱신 
			else {
				for(Point point : drivers[n].newPoints) {
					mapDriver[point.x][point.y]=n;
					drivers[n].points=new ArrayList<>(drivers[n].newPoints);
				}
			}
			
		}	
	}

	/**
	 * 기사가 명령대로 움직일 수 있는지 확인 
	 * return : 움직일 수 없으면 false 반환 
	 * newMapDriver : 해당 명령에서 움직인 기사 위치 
	 * drivers[n].newPoints : 해당 명령에서 움직인 기사 위치
	 * **/
	private static boolean move(int n, int dir, boolean[][] visited) {
		
		for(Point point : drivers[n].points) {
			visited[point.x][point.y]=true;
			
			int nx=point.x+deltas[dir][0];
			int ny=point.y+deltas[dir][1];
		
			// 밖으로 넘어가거나 벽이면 false 반환 
			if(nx<0||nx>=L||ny<0||ny>=L||map[nx][ny]==2) return false;
			
			if(n!=mapDriver[nx][ny]&&0!=mapDriver[nx][ny]&&!visited[nx][ny]) {
				
				// 한 번이라도 false 반환하면 움직이지 못 하므로 false 반환  
				if(!move(mapDriver[nx][ny],dir,visited)) {
					return false;
				}
			}
			
			newMapDriver[nx][ny]=n;
			drivers[n].newPoints.add(new Point(nx,ny));
		}
		return true;
		
	}
}