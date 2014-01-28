faacets_init;
s = Faacets.scenario('{[2 2] [2 2]}');
% implicitly, all inequalities are <= 0
disp('CHSH inequality');
chsh = s.inequality('<A1 B1> + <A1 B2> + <A2 B1> - <A2 B2> - 2 <>')
disp('CHSH inequality written using full probabilities');
chshfp = chsh.as('Non-signaling Probabilities')
disp('CHSH inequality in Collins-Gisin notation');
chshng = chsh.as('Non-signaling Collins-Gisin')

% show the ordering of the terms

disp('Coefficients for CHSH');
chsh.coeffs'
disp('and associated terms');
cell(chsh.terms)
disp('Coefficients for CHSH in full probabilities');
chshfp.coeffs'
disp('and associated terms');
cell(chshfp.terms)
disp('Coefficients for CHSH in Collins-Gisin notation');
chshng.coeffs'
disp('and associated terms');
cell(chshng.terms)

% save the forms of the inequality that we know
disp('Saving files...');
chsh.save('chsh_correlatorform.yaml');
chshng.save('chsh_collinsgisinform.yaml');
chshfp.save('chsh_fullform.yaml');
disp('Loading file again...');

ineq = Faacets.loadInequality('chsh_fullform.bell');

disp('And now for the tripartite scenario')
sliwa = Faacets.scenario('{[2 2] [2 2] [2 2]}')
 
% the GYNI inequality
disp('the Guess Your Neighbor Input inequality')

gyni = sliwa.inequality(['1/4 P(111|111) + 1/4 P(221|122) + ' ...
                    '1/4 P(122|212) + 1/4 P(212|221)'])

disp('with its canonical form')
canonicals = gyni.canonical

disp('We can have a look at its symmetry group generators')
% test that GYNI is symmetric under cyclic permutation of parties
canonicals(1).symmetryGenerators


disp('and save the inequality to a file.')
gyni.save('gyni_for3parties.bell');
