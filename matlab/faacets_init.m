global faacets_loaded;
if ~exist('faacets_loaded')
    % look for all Faacets releases in the current directory
    % and load the most recent
    jarfiles = dir('Faacets-assembly*.jar');
    jarnames = sort({jarfiles.name});
    javaaddpath(jarnames(length(jarnames)));
    import com.faacets.*;
    faacets_loaded = 1;
end