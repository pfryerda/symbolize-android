#include "line.h"
using namespace std;

// Pair functions
bool operator< (pair<int, int> p1, pair<int, int> p2) {
	if (p1.first != p2.first) return p1.first < p2.first;
	else					  return p1.second < p2.second;
}

unsigned int distSqr(pair<int, int> p1, pair<int, int> p2) {
	return (p1.first - p2.first)*(p1.first - p2.first) + (p1.second - p2.second)*(p1.second - p2.second);
}

Line::Line(pair<int, int> pt1 = make_pair(0, 0), pair<int, int> pt2 = make_pair(0, 0)): owner(App) {
	if(pt1 < pt2) {
		p1 = pt1;
		p2 = pt2;
	} else {
		p1 = pt2;
		p2 = pt1;
	}
} 
Line::Line(pair<int, int> pt1 = make_pair(0, 0), pair<int, int> pt2 = make_pair(0, 0), Owner owner = App): owner(owner) {
	if(pt1 < pt2) {
		p1 = pt1;
		p2 = pt2;
	} else {
		p1 = pt2;
		p2 = pt1;
	}
} 

pair<int ,int> Line::getP1() const { return p1; }
pair<int, int> Line::getP2() const { return p2; }
unsigned int Line::score(Line& line2) const { return distSqr(this->getP1(), line2.getP1())+distSqr(this->getP2(), line2.getP2()); }
/*void Line::printLineCode(ostream& out) const { 
	out << "Line(make_pair(" << p1.first << "," << p1.second << "), make_pair(" << p2.first << "," << p2.second << "), " << owner << ")"; 
}*/

bool operator<(const Line& line1, const Line& line2) {
	if(line1.getP1() != line2.getP1()) return (line1.getP1() < line2.getP1());
	else								return (line1.getP2() < line2.getP2());
}

bool operator==(const Line& user, const Line& soln) {
	return (
		((user.getP1().first - DRAWINGWIGGLEROOM)  <= soln.getP1().first)  && (soln.getP1().first  <= (user.getP1().first + DRAWINGWIGGLEROOM)) &&
		((user.getP1().second - DRAWINGWIGGLEROOM) <= soln.getP1().second) && (soln.getP1().second <= (user.getP1().second + DRAWINGWIGGLEROOM)) &&
		((user.getP2().first - DRAWINGWIGGLEROOM)  <= soln.getP2().first)  && (soln.getP2().first  <= (user.getP2().first + DRAWINGWIGGLEROOM)) &&
		((user.getP2().second - DRAWINGWIGGLEROOM) <= soln.getP2().second) && (soln.getP2().second <= (user.getP2().second + DRAWINGWIGGLEROOM))
		);
}