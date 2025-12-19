package com.jbs.formulawithfun


object QuizData {
    val formulasByStandard = mapOf(
        8 to listOf(
            // Rational Numbers & Exponents
            Formula("Product of Rational Numbers", "a/b × c/d = (a × c)/(b × d)"),
            Formula("Reciprocal of Rational Number", "Reciprocal of a/b = b/a (a ≠ 0)"),
            Formula("Add/Subtract Rational Numbers", "a/b ± c/d = (ad ± bc)/bd"),
            Formula("Product Law of Exponents", "a^m × a^n = a^(m+n)"),
            Formula("Quotient Law of Exponents", "a^m ÷ a^n = a^(m-n)"),
            Formula("Power of Power Law", "(a^m)^n = a^(mn)"),
            Formula("Power of Product", "(ab)^n = a^n × b^n"),
            Formula("Zero Exponent Law", "a^0 = 1"),
            Formula("Negative Exponent Law", "a^(-n) = 1/a^n"),

            // Algebraic Identities & Factorization
            Formula("Perfect Square", "Square of n = n × n = n²"),
            Formula("Square Root", "√x = y such that y × y = x"),
            Formula("Square of Binomial", "(a + b)² = a² + 2ab + b²"),
            Formula("Cube of Number", "Cube of n = n × n × n = n³"),
            Formula("Cube Root", "³√x = y such that y × y × y = x"),
            Formula("Cube of Binomial", "(a + b)³ = a³ + 3a²b + 3ab² + b³"),
            Formula("Standard Identity 1", "(a + b)² = a² + 2ab + b²"),
            Formula("Standard Identity 2", "(a - b)² = a² - 2ab + b²"),
            Formula("Standard Identity 3", "a² - b² = (a + b)(a - b)"),
            Formula("Standard Identity 4", "(a + b + c)² = a² + b² + c² + 2ab + 2bc + 2ca"),
            Formula("Standard Identity 5", "(a + b)^3 = a^3 + 3a^2b + 3ab^2 + b^3"),
            Formula("Addition of Expressions", "(ax + b) + (cx + d) = (a+c)x + (b+d)"),
            Formula("Multiplication of Expressions", "(a + b)(c + d) = ac + ad + bc + bd"),
            Formula("Taking Out Common", "ab + ac = a(b + c)"),
            Formula("Factorization using Identities", "a² - b² = (a + b)(a - b)"),

            // Linear Equations
            Formula("Linear Equation", "ax + b = c"),
            Formula("Solution of Linear Equation", "x = (c - b)/a"),

            // Percentages, Profit, Loss, and Interest
            Formula("Percentage", "Percentage = (Value/Total Value) × 100"),
            Formula("Profit", "Profit = Selling Price - Cost Price"),
            Formula("Loss", "Loss = Cost Price - Selling Price"),
            Formula("Profit Percentage", "Profit% = (Profit/Cost Price) × 100"),
            Formula("Loss Percentage", "Loss% = (Loss/Cost Price) × 100"),
            Formula("Discount", "Discount = Marked Price - Selling Price"),
            Formula("Simple Interest", "SI = (P × R × T)/100"),
            Formula("Amount (SI)", "Amount = Principal + Simple Interest"),
            Formula("Compound Interest", "CI = P × [(1 + R/100)^T - 1]"),
            Formula("Compound Interest (compounded)", "A = P(1 + R/(n×100))^(nt)"),
            Formula("Commission", "Commission = (Rate of Commission × Total Sales) / 100"),
            Formula("Brokerage", "Brokerage = (Brokerage Rate × Total Amount) / 100"),
            Formula("Purchase Price (with Brokerage)", "Purchase Price = Market Price + Brokerage"),

            // Averages and Statistics
            Formula("Mean", "Mean = (Sum of all observations) / (Number of observations)"),
            Formula("Average", "Average = (Sum of observations) / (Number of observations)"),
            Formula("Range", "Range = Highest Value - Lowest Value"),
            Formula("Mode", "Mode = Most Frequently Occurring Value"),

            // Geometry: Perimeter, Area, Volume
            Formula("Perimeter of Rectangle", "Perimeter = 2 × (Length + Breadth)"),
            Formula("Area of Rectangle", "Area = Length × Breadth"),
            Formula("Perimeter of Square", "Perimeter = 4 × Side"),
            Formula("Area of Square", "Area = Side × Side = Side²"),
            Formula("Perimeter of Triangle", "Perimeter = Sum of all sides"),
            Formula("Area of Triangle", "Area = 1/2 × Base × Height"),
            Formula("Area of Right Angle Triangle", "Area = (1/2) × Base × Height"),
            Formula("Area of Triangle (semiperimeter)", "Area = √[s(s-a)(s-b)(s-c)], where s = (a+b+c)/2"),
            Formula("Area of Parallelogram", "Area = Base × Height"),
            Formula("Area of Rhombus", "Area = 1/2 × d₁ × d₂ (where d₁, d₂ are diagonals)"),
            Formula("Area of Trapezium", "Area = 1/2 × (Sum of parallel sides) × Height"),
            Formula("Area of Circle", "Area = π × r²"),
            Formula("Circumference of Circle", "Circumference = 2πr"),

            // Mensuration: Solids
            Formula("Volume of Cube", "Volume = Side³"),
            Formula("Surface Area of Cube", "Surface Area = 6 × Side²"),
            Formula("Volume of Cuboid", "Volume = Length × Breadth × Height"),
            Formula("Surface Area of Cuboid", "Surface Area = 2(lb + bh + hl)"),
            Formula("Volume of Cylinder", "Volume = π × r² × h"),
            Formula("Curved Surface Area of Cylinder", "CSA = 2πrh"),
            Formula("Total Surface Area of Cylinder", "TSA = 2πr(r + h)"),
            Formula("Volume of Cone", "Volume = (1/3) × π × r² × h"),
            Formula("Curved Surface Area of Cone", "CSA = πrl"),
            Formula("Total Surface Area of Cone", "TSA = πr(l + r)"),
            Formula("Volume of Sphere", "Volume = (4/3) × π × r³"),
            Formula("Surface Area of Sphere", "Surface Area = 4πr²"),
            Formula("Volume of Hemisphere", "Volume = (2/3) × π × r³"),
            Formula("Surface Area of Hemisphere", "Curved SA = 2πr², Total SA = 3πr²"),
            Formula("Lateral Surface Area of Cube", "LSA = 4 × Side²"),
            Formula("Lateral Surface Area of Cuboid", "LSA = 2h(l + b)"),

            // Other Geometry
            Formula("Sum of Angles of Triangle", "Sum = 180°"),
            Formula("Sum of Angles of Polygon", "Sum = (n - 2) × 180°"),
            Formula("Exterior Angle of Regular Polygon", "Exterior Angle = 360°/n"),
            Formula("Distance between two points", "√[(x₂ - x₁)² + (y₂ - y₁)²]"),

            // Ratio, Proportion, Set Theory
            Formula("Direct Proportion", "x₁/y₁ = x₂/y₂"),
            Formula("Inverse Proportion", "x₁ × y₁ = x₂ × y₂"),
            Formula("Ratio", "Ratio of a to b = a/b"),
            Formula("Union of Sets", "n(A ∪ B) = n(A) + n(B) - n(A ∩ B)"),
            Formula("Intersection of Sets", "A ∩ B = Common elements of A and B"),

            // Time, Speed, Distance
            Formula("Speed, Distance, Time", "Speed = Distance / Time; Time = Distance / Speed; Distance = Speed × Time"),
            Formula("Probability", "Probability = (Number of favorable outcomes) / (Total outcomes)")
        ),
        9 to listOf(
            // Rational Numbers, Exponents, Algebra
            Formula("Product of Rational Numbers", "a/b × c/d = (a × c)/(b × d)"),
            Formula("Reciprocal of Rational Number", "Reciprocal of a/b = b/a"),
            Formula("Laws of Exponents", "a^m × a^n = a^(m+n)"),
            Formula("Negative Exponent", "a^(-n) = 1/(a^n)"),
            Formula("nth Root", "a^(1/n) = nth root of a"),
            Formula("Zero of a Polynomial", "p(x) = 0, then x is zero of polynomial"),
            Formula("Degree of Polynomial", "Highest power of x"),
            // Algebraic Identities
            Formula("Standard Identity 1", "(a + b)² = a² + 2ab + b²"),
            Formula("Standard Identity 2", "(a - b)² = a² - 2ab + b²"),
            Formula("Standard Identity 3", "a² - b² = (a + b)(a - b)"),
            Formula("Standard Identity 4", "(a + b + c)² = a² + b² + c² + 2ab + 2bc + 2ca"),
            Formula("Standard Identity 5", "(a + b)^3 = a^3 + 3a^2b + 3ab^2 + b^3"),
            Formula("Algebraic Identity: (a-b)³", "a³ - 3a²b + 3ab² - b³"),

            // Coordinate Geometry
            Formula("Distance Formula", "Distance = √[(x₂ - x₁)² + (y₂ - y₁)²]"),
            Formula("Section Formula (internal)", "((mx₂ + nx₁)/(m + n), (my₂ + ny₁)/(m + n))"),
            Formula("Midpoint Formula", "Midpoint = ((x₁ + x₂)/2, (y₁ + y₂)/2)"),

            // Linear Equations & Geometry
            Formula("General Form", "ax + by + c = 0"),
            Formula("Slope-Intercept Form", "y = mx + c"),
            Formula("Sum of Angles on a Straight Line", "Sum = 180°"),
            Formula("Sum of Angles around a Point", "Sum = 360°"),
            Formula("Vertically Opposite Angles", "Are equal"),
            Formula("Alternate Interior Angles", "Are equal"),
            Formula("Corresponding Angles", "Are equal"),
            Formula("Angle Sum Property", "Sum of angles = 180°"),

            // Areas, Volumes, Surface Areas
            Formula("Area of Triangle", "Area = 1/2 × Base × Height"),
            Formula("Area of Triangle (Heron's)", "Area = √[s(s-a)(s-b)(s-c)]"),
            Formula("s (semi-perimeter)", "s = (a + b + c)/2"),
            Formula("Area of Parallelogram", "Area = Base × Height"),
            Formula("Area of Rhombus", "Area = 1/2 × d₁ × d₂"),
            Formula("Area of Trapezium", "Area = 1/2 × (Sum of parallel sides) × Height"),
            Formula("Area of Quadrilateral", "Area = 1/2 × diagonal × (sum of heights)"),
            // Mensuration: Solids
            Formula("Surface Area of Cube", "Surface Area = 6 × Side²"),
            Formula("Volume of Cube", "Volume = Side³"),
            Formula("Surface Area of Cuboid", "Surface Area = 2(lb + bh + hl)"),
            Formula("Volume of Cuboid", "Volume = l × b × h"),
            Formula("Curved Surface Area of Cylinder", "CSA = 2πrh"),
            Formula("Total Surface Area of Cylinder", "TSA = 2πr(r + h)"),
            Formula("Volume of Cylinder", "Volume = πr²h"),
            Formula("Curved Surface Area of Cone", "CSA = πrl"),
            Formula("Total Surface Area of Cone", "TSA = πr(l + r)"),
            Formula("Volume of Cone", "Volume = (1/3) × π × r² × h"),
            Formula("Surface Area of Sphere", "Surface Area = 4πr²"),
            Formula("Volume of Sphere", "Volume = (4/3)πr³"),
            Formula("Surface Area of Hemisphere", "Curved SA = 2πr², Total SA = 3πr²"),
            Formula("Volume of Hemisphere", "Volume = (2/3)πr³"),
            Formula("Lateral Surface Area of Cube", "LSA = 4 × Side²"),
            Formula("Lateral Surface Area of Cuboid", "LSA = 2h(l + b)"),

            // Commercial Mathematics
            Formula("Commission", "Commission = (Rate × Value of Sales) / 100"),
            Formula("Brokerage", "Brokerage = (Brokerage Rate × Amount) / 100"),
            Formula("Net Profit", "Net Profit = Gross Profit - Expenses"),
            Formula("Percentage Profit", "Profit% = (Profit/Cost Price) × 100"),
            Formula("Percentage Loss", "Loss% = (Loss/Cost Price) × 100"),
            Formula("Average", "Average = (Sum of Values) / (Number of Values)"),

            // Statistics
            Formula("Mean", "Mean = (Sum of all observations) / (Number of observations)"),
            Formula("Median (odd N)", "Median = Middle value"),
            Formula("Median (even N)", "Median = Mean of two middle values"),
            Formula("Mode", "Mode = Most Frequently Occurring Value"),
            Formula("Range", "Range = Highest Value - Lowest Value"),
            Formula("Weighted Mean", "Weighted Mean = Σ(wx)/Σw"),
            Formula("Basic Statistics", "Mean, Median, Mode, Range, Standard Deviation"),

            // Sets, Probability, Proportion
            Formula("n(Sample Space)", "Total possible outcomes"),
            Formula("Probability", "Probability = (Number of favorable outcomes) / (Total number of outcomes)"),
            Formula("Direct Proportion", "x₁/y₁ = x₂/y₂"),
            Formula("Inverse Proportion", "x₁ × y₁ = x₂ × y₂"),
            Formula("Union of Sets", "n(A ∪ B) = n(A) + n(B) - n(A ∩ B)"),
            Formula("Intersection of Sets", "A ∩ B = Common elements of A and B"),
            Formula("Complement of Set", "A' = Elements not in A"),

            // Time, Speed, Distance
            Formula("Speed, Distance, Time", "Speed = Distance / Time; Time = Distance / Speed; Distance = Speed × Time")
        ),
        10 to listOf(
            // Number System, Polynomials
            Formula("Euclid’s Division Lemma", "For any two positive integers a and b, a = bq + r, 0 ≤ r < b"),
            Formula("HCF × LCM", "HCF(a, b) × LCM(a, b) = a × b"),
            Formula("Rational Number", "A number that can be expressed as p/q, where q ≠ 0"),
            Formula("Irrational Number", "Cannot be expressed as p/q"),
            Formula("Zero of Polynomial", "Value of x for which p(x) = 0"),
            Formula("Quadratic Polynomial", "ax² + bx + c, a ≠ 0"),
            Formula("Sum and Product of Roots", "If ax² + bx + c = 0, then Sum = -b/a, Product = c/a"),
            Formula("Relationship between Coefficient and Zeroes", "If α and β are roots, then x² - (α + β)x + αβ = 0"),
            Formula("Factor Theorem", "If f(a) = 0, then (x - a) is a factor of f(x)"),
            Formula("Remainder Theorem", "If f(x) is divided by (x - a), the remainder is f(a)"),

            // Linear Equations, Quadratics, AP
            Formula("General Form", "a₁x + b₁y + c₁ = 0"),
            Formula("Consistency Condition", "a₁/a₂ ≠ b₁/b₂ → Unique Solution"),
            Formula("Substitution Method", "Solve one equation for one variable, substitute in the other"),
            Formula("Elimination Method", "Add/Subtract equations to eliminate one variable"),
            Formula("Standard Form", "ax² + bx + c = 0, a ≠ 0"),
            Formula("Quadratic Formula", "x = [-b ± √(b² - 4ac)] / (2a)"),
            Formula("Discriminant", "D = b² - 4ac"),
            Formula("Nature of Roots (summary)", "D > 0: 2 real & distinct, D = 0: 2 real & equal, D < 0: No real roots"),
            Formula("nth Term of AP", "aₙ = a + (n-1)d"),
            Formula("Sum of n terms of AP", "Sₙ = n/2 [2a + (n-1)d]"),

            // Similarity & Geometry
            Formula("Basic Proportionality Theorem", "If DE ∥ BC in ΔABC, then AD/DB = AE/EC"),
            Formula("Criteria for Similarity", "AA, SSS, SAS"),
            Formula("Pythagoras Theorem", "In right ΔABC, AC² = AB² + BC²"),
            Formula("Area Ratio of Similar Triangles", "Area₁/Area₂ = (Side₁/Side₂)²"),

            // Coordinate Geometry
            Formula("Distance Formula", "Distance = √[(x₂ - x₁)² + (y₂ - y₁)²]"),
            Formula("Section Formula", "((mx₂ + nx₁)/(m + n), (my₂ + ny₁)/(m + n))"),
            Formula("Midpoint Formula", "((x₁ + x₂)/2, (y₁ + y₂)/2)"),
            Formula("Area of Triangle (Coordinate)", "Area = 1/2 |x₁(y₂ - y₃) + x₂(y₃ - y₁) + x₃(y₁ - y₂)|"),

            // Trigonometry
            Formula("sin θ", "Opposite Side / Hypotenuse"),
            Formula("cos θ", "Adjacent Side / Hypotenuse"),
            Formula("tan θ", "Opposite Side / Adjacent Side"),
            Formula("cosec θ", "1 / sin θ"),
            Formula("sec θ", "1 / cos θ"),
            Formula("cot θ", "1 / tan θ"),
            Formula("Trigonometric Ratios Table", "sin 0° = 0, sin 30° = 1/2, sin 45° = 1/√2, sin 60° = √3/2, sin 90° = 1"),
            Formula("sin²θ + cos²θ", "1"),
            Formula("1 + tan²θ", "sec²θ"),
            Formula("1 + cot²θ", "cosec²θ"),
            Formula("Reciprocal Trigonometric Identities", "sin θ = 1/cosec θ, cos θ = 1/sec θ, tan θ = 1/cot θ, etc."),
            Formula("Sum & Difference Formulas", "sin(A ± B) = sinA cosB ± cosA sinB"),
            Formula("Height and Distance", "tan θ = Perpendicular/Base"),

            // Circles & Tangents
            Formula("Tangent Perpendicularity", "Tangent at any point is perpendicular to radius at point of contact"),
            Formula("Number of Tangents from External Point", "Exactly two tangents"),
            Formula("Construction Principle", "Use compass and straightedge to bisect, draw tangents, etc."),

            // Mensuration: Areas, Volumes, Surface Areas
            Formula("Area of Circle", "πr²"),
            Formula("Circumference", "2πr"),
            Formula("Area of Sector", "(θ/360°) × πr²"),
            Formula("Length of Arc", "(θ/360°) × 2πr"),
            Formula("Area of Trapezium", "Area = 1/2 × (Sum of parallel sides) × Height"),
            Formula("Area of Parallelogram", "Area = Base × Height"),
            Formula("Area of Rhombus", "Area = 1/2 × d₁ × d₂"),
            Formula("Area of Triangle (semiperimeter)", "Area = √[s(s-a)(s-b)(s-c)], s = (a+b+c)/2"),

            // Mensuration: Solids (All major boards)
            Formula("Volume of Cube", "Volume = Side³"),
            Formula("Surface Area of Cube", "Surface Area = 6 × Side²"),
            Formula("Volume of Cuboid", "Volume = Length × Breadth × Height"),
            Formula("Surface Area of Cuboid", "Surface Area = 2(lb + bh + hl)"),
            Formula("Volume of Cylinder", "Volume = π × r² × h"),
            Formula("Curved Surface Area of Cylinder", "CSA = 2πrh"),
            Formula("Total Surface Area of Cylinder", "TSA = 2πr(r + h)"),
            Formula("Volume of Cone", "Volume = (1/3) × π × r² × h"),
            Formula("Curved Surface Area of Cone", "CSA = πrl"),
            Formula("Total Surface Area of Cone", "TSA = πr(l + r)"),
            Formula("Volume of Sphere", "Volume = (4/3) × π × r³"),
            Formula("Surface Area of Sphere", "Surface Area = 4πr²"),
            Formula("Volume of Hemisphere", "Volume = (2/3) × π × r³"),
            Formula("Surface Area of Hemisphere", "Curved SA = 2πr², Total SA = 3πr²"),
            Formula("Lateral Surface Area of Cube", "LSA = 4 × Side²"),
            Formula("Lateral Surface Area of Cuboid", "LSA = 2h(l + b)"),

            // Commercial Maths
            Formula("Commission", "Commission = (Commission % × Total Sales) / 100"),
            Formula("Brokerage", "Brokerage = (Brokerage Rate × Amount) / 100"),
            Formula("Net Profit", "Net Profit = Gross Profit - Expenses"),
            Formula("Average", "Average = (Sum of Observations) / (Number of Observations)"),

            // Statistics (with advanced)
            Formula("Mean (Direct Method)", "Mean = (Σfᵢxᵢ)/(Σfᵢ)"),
            Formula("Mean (Assumed Mean Method)", "Mean = a + [Σfᵢdᵢ/Σfᵢ] × h"),
            Formula("Median (for continuous series)", "Median = l + [(n/2 - F)/f] × h"),
            Formula("Mode", "Mode = l + [(f₁ - f₀)/(2f₁ - f₀ - f₂)] × h"),
            Formula("Weighted Mean", "Weighted Mean = Σ(wx)/Σw"),
            Formula("Harmonic Mean", "Harmonic Mean = n / Σ(1/xᵢ)"),
            Formula("Standard Deviation (basic)", "SD = sqrt[(Σ(xᵢ - x̄)²)/N]"),

            // Sets, Probability
            Formula("Set Complement", "n(U) - n(A) = n(A')"),
            Formula("Direct Proportion", "x₁/y₁ = x₂/y₂"),
            Formula("Inverse Proportion", "x₁ × y₁ = x₂ × y₂"),
            Formula("Union of Sets", "n(A ∪ B) = n(A) + n(B) - n(A ∩ B)"),
            Formula("Intersection of Sets", "A ∩ B = Common elements of A and B"),
            Formula("Probability", "Probability = (Number of Favorable Outcomes) / (Total Number of Outcomes)"),

            // Logarithms (if included in board)
            Formula("Logarithm Laws", "logₐ(mn) = logₐm + logₐn; logₐ(m/n) = logₐm - logₐn; logₐ(m^k) = k × logₐm"),

            // Time, Speed, Distance
            Formula("Speed, Distance, Time", "Speed = Distance / Time; Time = Distance / Speed; Distance = Speed × Time")
        )
    )
}
