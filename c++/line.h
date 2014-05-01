#ifndef __LINE_H__
#define __LINE_H__
#include <iostream>
#include <utility>
#include <list>

const int DRAWINGWIGGLEROOM = 14;

enum Owner {
	App,
	User
};

class Line {
	std::pair<int, int> p1, p2;
 	Owner owner; 
public:
	Line(std::pair<int, int>, std::pair<int, int>);
	Line(std::pair<int, int>, std::pair<int, int>, Owner);
	std::pair<int, int> getP1() const;
	std::pair<int, int> getP2() const;
	unsigned int score(Line&) const;
	//void printLineCode(std::ostream&) const;

	friend bool operator<(const Line&, const Line&);
	friend bool operator==(const Line& user, const Line& soln);	// User's line goes on left, solution on right!
};

typedef std::list<Line*> Graph;

#endif