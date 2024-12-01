import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	static int L, N, Q, ans;
	static int[][] map, numMap, deltas= {{-1,0},{0,1},{1,0},{0,-1}};
	static boolean[] visited;
	static People[] peoples;

	public static class People {
		int k, damage;
		boolean out;
		List<int[]> point=new ArrayList<int[]>();
		List<int[]> newPoint=new ArrayList<int[]>();
		
		public People(int k) {
			super();
			this.k = k;
		}
		
	}
	public static void main(String[] args) throws IOException {
		tokens=new StringTokenizer(input.readLine());
		L=Integer.parseInt(tokens.nextToken());
		N=Integer.parseInt(tokens.nextToken());
		Q=Integer.parseInt(tokens.nextToken());
	
		map=new int[L][L];
		numMap=new int[L][L];
		peoples=new People[N+1];
		visited=new boolean[N+1];
		ans=0;
		
		for(int r=0;r<L;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<L;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		for(int n=1;n<=N;n++) {
			tokens=new StringTokenizer(input.readLine());
			int r=Integer.parseInt(tokens.nextToken())-1;
			int c=Integer.parseInt(tokens.nextToken())-1;
			int h=Integer.parseInt(tokens.nextToken());
			int w=Integer.parseInt(tokens.nextToken());
			int k=Integer.parseInt(tokens.nextToken());
			peoples[n]=new People(k);
			
			for(int x=r;x<r+h;x++) {
				for(int y=c;y<c+w;y++) {
					peoples[n].point.add(new int[] {x,y});
					numMap[x][y]=n;
				}
			}
		}
		
		for(int q=0;q<Q;q++) {
			tokens=new StringTokenizer(input.readLine());
			int num=Integer.parseInt(tokens.nextToken());
			int dir=Integer.parseInt(tokens.nextToken());
			
			// 기사 이동 
			if(!peoples[num].out) movePeople(num, dir);
			
//			for(int r=0;r<L;r++) System.out.println(Arrays.toString(numMap[r]));
//			System.out.println();
			
			for(int n=1;n<=N;n++) {
				if(peoples[n].out) continue;
				peoples[n].newPoint.clear();
			}
		}
		
		for(int n=1;n<=N;n++) {
			if(peoples[n].out) continue;
			ans+=peoples[n].damage;
		}
		System.out.println(ans);
	}
	private static void movePeople(int num, int dir) {
		
		Queue<Integer> q=new LinkedList<>();
		visited=new boolean[N+1];
		visited[num]=true;
		
		// 명령 받은 기사 움직임 
		for(int[] point:peoples[num].point) {
			//System.out.println(point[0]+" "+point[1]+" "+num);
			int nx=point[0]+deltas[dir][0];
			int ny=point[1]+deltas[dir][1];
			
			if(nx<0||nx>=L||ny<0||ny>=L) return;
			if(map[nx][ny]==2) return;
			
			// System.out.println(nx+" "+ny+" "+num);
			peoples[num].newPoint.add(new int[] {nx,ny});
			if(numMap[nx][ny]>0&&!visited[numMap[nx][ny]]) {
				visited[numMap[nx][ny]]=true;
				q.add(numMap[nx][ny]);
			}
			
		}
		
		// 기사의 움직임으로 연쇄적으로 움직이는 기사들 
		while(q.size()>0) {
			
			int nextNum=q.poll();
			// 명령 받은 기사 움직임 
			for(int[] point:peoples[nextNum].point) {
				int nx=point[0]+deltas[dir][0];
				int ny=point[1]+deltas[dir][1];
				
				if(nx<0||nx>=L||ny<0||ny>=L) return;
				if(map[nx][ny]==2) return;
				
				//System.out.println(nx+" "+ny+" "+nextNum);
				
				peoples[nextNum].newPoint.add(new int[] {nx,ny});
				if(numMap[nx][ny]>0&&!visited[numMap[nx][ny]]) {
					visited[numMap[nx][ny]]=true;
					q.add(numMap[nx][ny]);
				}
				
			}
		}
		
		numMap=new int[L][L];
		// 모든 기사들이 벽으로 안 밀려나면 numMap 업데이트 및 peoples.point 업데이트 
		loop:for(int n=1;n<=N;n++) {
			if(peoples[n].out) continue;
			
			int damage=0;
			for(int i=0;i<peoples[n].point.size();i++) {
				if(peoples[n].newPoint.isEmpty()) {
					int[] now=peoples[n].point.get(i);
					numMap[now[0]][now[1]]=n;
				}
				else {
					int[] next=peoples[n].newPoint.get(i);
					numMap[next[0]][next[1]]=n;
					if(map[next[0]][next[1]]==1) damage+=1;
				}
			}
			
			// num은 대미지 안 받음 
			if(n!=num&&damage!=0) {
				peoples[n].damage+=damage;
				if(peoples[n].k-peoples[n].damage<=0) {
					peoples[n].out=true; 
					// 데미지 받은 기사는 체스판에서 사라짐 
					for(int i=0;i<peoples[n].point.size();i++) {
						int[] next=peoples[n].newPoint.get(i);
						numMap[next[0]][next[1]]=0;
					}
					continue loop;
				}
			}
			
			if(!peoples[n].newPoint.isEmpty()) {
				peoples[n].point.clear();
				peoples[n].point.addAll(peoples[n].newPoint);
			}
		}
	}
}
