import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.CollationElementIterator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int N, M, K, potabCnt, ans; // N*M
	static List<Potab> potabs, sortPotabs;
	static Potab attack, goal;
	static int map[][], deltas[][]= {{0,1},{1,0},{0,-1},{-1,0},{-1,-1},{-1,1},{1,-1},{1,1}}; //우하좌상
	
	public static class Info {
		int x, y;
		List<Integer> nums=new ArrayList<Integer>();
		
		public Info(int x, int y, List<Integer> nums) {
			super();
			this.x = x;
			this.y = y;
			this.nums.addAll(nums);
			this.nums.add(map[x][y]);
		}

		public Info(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	public static class Potab implements Comparable<Potab>{
		int x, y, tern, power;
		boolean removed, attacked;
		
		public Potab(int x, int y, int power) {
			super();
			this.x = x;
			this.y = y;
			this.power=power;
		}

		@Override
		public int compareTo(Potab o) {
			if(this.power==o.power) {
				if(this.tern==o.tern) {
					if(this.x+this.y==o.x+o.y) {
						return Integer.compare(this.y, o.y)*-1;
					}
					return Integer.compare(this.x+this.y, o.x+o.y)*-1;
				}
				return Integer.compare(this.tern, o.tern)*-1;
			}
			return Integer.compare(this.power, o.power);
		}

		@Override
		public String toString() {
			return "Potab [x=" + x + ", y=" + y + ", tern=" + tern + ", power=" + power + ", removed=" + removed
					+ ", attacked=" + attacked + "]";
		}
		
	}
	
	// 0 이하 -> 공격X, 부서짐 
	// 포탑 1개면 종료 
	// K 턴 
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		N=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		K=Integer.parseInt(tokens.nextToken());
		
		potabs=new ArrayList<>();
		potabs.add(new Potab(0, 0, 0)); // 사용안하는 포탑 
		sortPotabs=new ArrayList<>();
		map=new int[N][M];
		potabCnt=0;
		
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<M;c++) {
				int power=Integer.parseInt(tokens.nextToken());
				if(power>0) {
					potabs.add(new Potab(r, c, power));
					map[r][c]=++potabCnt;
				}
			}
		}
		
		for(int k=1;k<=K;k++) {
			if(potabCnt==1) break;
			
			sortPotabs=new ArrayList<>();
			for(int i=1;i<potabs.size();i++) {
				if(potabs.get(i).removed) continue;
				sortPotabs.add(potabs.get(i));
			}
			Collections.sort(sortPotabs);
			
			
			attack=sortPotabs.get(0);
			potabs.get(map[attack.x][attack.y]).attacked=true;
			potabs.get(map[attack.x][attack.y]).tern=k;
			potabs.get(map[attack.x][attack.y]).power+=N+M;
					
			goal=sortPotabs.get(sortPotabs.size()-1);
			potabs.get(map[goal.x][goal.y]).attacked=true;
			
			if(!attack1()) {
				attack2();
			}
			
			// 6. 정비 
			// 부서지지 않은 포탑 중 공격과 무관했던 포탑은 공격력이 1씩 올라갑니다
			for(int i=1;i<potabs.size();i++) {
				if(potabs.get(i).removed) continue;
				if(potabs.get(i).attacked) {
					potabs.get(i).attacked=false;
					continue;
				}
				potabs.get(i).power+=1;
			}
		}
		
		ans=Integer.MIN_VALUE;
		
//		for(int r=0;r<N;r++) System.out.println(Arrays.toString(map[r]));
		for(int i=1;i<potabs.size();i++) {
//			System.out.println(potabs.get(i).toString());
			if(potabs.get(i).removed) continue;
			if(ans<potabs.get(i).power) ans=potabs.get(i).power;
		}
		
		output.append(ans);
		
		System.out.println(output);
	}

	private static void attack2() {
		
		// 목표 포탑 공격력 하락 
		potabs.get(map[goal.x][goal.y]).power-=potabs.get(map[attack.x][attack.y]).power;
		if(potabs.get(map[goal.x][goal.y]).power<=0) {
			potabs.get(map[goal.x][goal.y]).removed=true;
			map[goal.x][goal.y]=0;
			potabCnt-=1;
		}
		
		for(int d=0;d<8;d++) {
			int nx=goal.x+deltas[d][0];
			int ny=goal.y+deltas[d][1];
			
			if(nx<0) nx=N-1;
			else if(nx>=N) nx=0;
			if(ny<0) ny=M-1;
			else if(ny>=M) ny=0;
			
			// 8가지 방향 포탑 공격력 하락 
			potabs.get(map[nx][ny]).power-=(potabs.get(map[attack.x][attack.y]).power/2);
			potabs.get(map[nx][ny]).attacked=true;
			if(potabs.get(map[nx][ny]).power<=0) {
				potabs.get(map[nx][ny]).removed=true;
				map[nx][ny]=0;
				potabCnt-=1;
			}
		}
	}

	// 3. 레이저 
	// 1. 우하좌상, 0인곳은 지나갈 수 X, 최단 거리 
	// 2. 공격 대상에는 공격자의 공격력 만큼의 피해를 입히며, 피해를 입은 포탑은 해당 수치만큼 공격력이 줄어듭니다
	// 3. 공격 대상을 제외한 레이저 경로에 있는 포탑도 공격을 받게 되는데, 이 포탑은 공격자 공격력의 절반 만큼의 공격을 받습니다.
	private static boolean attack1() {
		
		Queue<Info> q=new LinkedList<>();
		boolean visited[][]=new boolean[N][M];
		q.add(new Info(attack.x, attack.y));
		visited[attack.x][attack.y]=true;
		
		while(q.size()>0) {
			Info now=q.poll();
			
			for(int d=0;d<4;d++) {
				int nx=now.x+deltas[d][0];
				int ny=now.y+deltas[d][1];
				
				if(nx<0) nx=N-1;
				else if(nx>=N) nx=0;
				if(ny<0) ny=M-1;
				else if(ny>=M) ny=0;
				
				if(nx==goal.x&&ny==goal.y) { // 목표포탑 도착 
					
					// 목표 포탑 공격력 하락 
					potabs.get(map[goal.x][goal.y]).power-=potabs.get(map[attack.x][attack.y]).power;
					if(potabs.get(map[goal.x][goal.y]).power<=0) {
						potabs.get(map[goal.x][goal.y]).removed=true;
						map[goal.x][goal.y]=0;
						potabCnt-=1;
					}
					
					// 지나온 포탑 공격력 하락 
					for(int num:now.nums) {
//						System.out.println(num);
						potabs.get(num).power-=(potabs.get(map[attack.x][attack.y]).power/2);
						potabs.get(num).attacked=true;
						if(potabs.get(num).power<=0) {
							potabs.get(num).removed=true;
							map[potabs.get(num).x][potabs.get(num).y]=0;
							potabCnt-=1;
						}
					}
					
					return true;
				}
				else if(map[nx][ny]>0&&!visited[nx][ny]) {
					q.add(new Info(nx,ny,now.nums));
					visited[nx][ny]=true;
				} 
			}
		}
		
		return false;
	}

}
