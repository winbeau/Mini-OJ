#include <iostream>

int main() {
    int year;
    if (std::cin >> year) {
        bool leap = year % 400 == 0 || (year % 4 == 0 && year % 100 != 0);
        std::cout << (leap ? "YES" : "NO") << '\n';
    }
    return 0;
}
