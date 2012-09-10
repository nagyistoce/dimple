%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%   Copyright 2012 Analog Devices, Inc.
%
%   Licensed under the Apache License, Version 2.0 (the "License");
%   you may not use this file except in compliance with the License.
%   You may obtain a copy of the License at
%
%       http://www.apache.org/licenses/LICENSE-2.0
%
%   Unless required by applicable law or agreed to in writing, software
%   distributed under the License is distributed on an "AS IS" BASIS,
%   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
%   See the License for the specific language governing permissions and
%   limitations under the License.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

classdef DiscreteVariableBase < VariableBase
    
    methods
        function obj = DiscreteVariableBase(domain,varargin)
            %{
            if ~iscell(domain)
                newdomain = cell(1,numel(domain));
                for i = 1:numel(domain);
                    newdomain{i} = domain(i);
                end
                domain = newdomain;
            end
            %}
            %obj.Domain = DiscreteDomain(domain);
            
            if ~isa(domain,'Domain')
                domain = DiscreteDomain(domain);
            end
            
            obj.Domain = domain;

            
            if length(varargin) == 3 && strcmp('existing',varargin{1})
                
                obj.Indices = varargin{3};
                obj.VarMat = varargin{2};
                    
                
            else
                modeler = getModeler();
                dims = [varargin{:}];
                if size(dims) == 1
                    varargin = {varargin{1}, varargin{1}};
                    dims = [varargin{:}];
                end
                
                numEls = prod(dims);
                
                %try
                    varMat = modeler.createVariableVector(class(obj),domain.IDomain,numEls);
                %catch exception
                %   newdomain = cell(size(domain));
                %    for i = 1:numel(newdomain)
                %        newdomain{i} = ['notvalidjava' num2str(i)];
                %    end
                %    varMat = modeler.createDiscreteVariableVector(class(obj),newdomain,numEls);
                %end
                    
                obj.VarMat = varMat;
                
                obj.Indices = 0:(numEls-1);
                
                if (length(varargin) > 1)
                    obj.Indices = reshape(obj.Indices,varargin{:});
                end
                
            end
        end
        
    end
           
    methods (Access = protected)
        
        function setInput(obj,b)
            
            d = obj.Domain.Elements;
            v = obj.Indices;
            
            
            if (numel(b) == numel(d))
                b = reshape(b,1,numel(d));
                b = repmat(b,numel(v),1);
                
                
            else
                
                if length(v) == numel(v)
                    v = reshape(v,numel(v),1);
                    
                    if ~all(size(b) == [length(v) length(d)])
                        error('dims must match');
                    end
                else
                    if numel(d) == 1
                        if ~all(size(b) == size(v))
                            error('dims must match');
                        end
                    else
                        if ~all(size(b) == [size(v)  numel(d)])
                            error('dims must match');
                        end
                    end
                    
                    b = reshape(b,numel(b)/length(d),length(d));
                end
            end
            
            varids = reshape(v,numel(v),1);
            
            obj.VarMat.setInput(varids,b);
            
        end
        
        function b = getInput(obj)
            varids = reshape(obj.Indices,numel(obj.Indices),1);
            input = obj.VarMat.getInput(varids);
            
            %TODO: reuse this code from getBeliefs
            b = zeros(numel(obj.Indices),length(obj.Domain.Elements));
            
            for i = 1:size(input,1)
                b(i,:) = input(i,:);
            end
            v = obj.Indices;
            
            %1x1 - Leave priors as is
            %1xN - Transpose
            %Nx1 - Leave as is
            %Anything else - Add extra dimension           
            sz = size(v);
            isvector = numel(v) == length(v) && numel(v) > 1;
            isrowvector = isvector && sz(1) == 1;
            iscolvector = isvector && sz(2) == 1;
            
            if isscalar(v) || iscolvector
                
            elseif isrowvector
                b = b';
            else
                sz = size(v);
                sz = [sz numel(b)/numel(v)];
                b = reshape(b,sz);
                
            end
        end
        
        function b = getBelief(obj)
            
            v = obj.Indices;
            varids = reshape(v,numel(v),1);
            
            tmp = obj.VarMat.getBeliefs(varids);
            b = zeros(numel(v),length(obj.Domain.Elements));
            for i = 1:size(tmp,1)
                b(i,:) = tmp(i,:);
            end
            
            %1x1 - Leave priors as is
            %1xN - Transpose
            %Nx1 - Leave as is
            %Anything else - Add extra dimension           
            sz = size(v);
            isvector = numel(v) == length(v) && numel(v) > 1;
            isrowvector = isvector && sz(1) == 1;
            iscolvector = isvector && sz(2) == 1;
            
            if isscalar(v) || iscolvector
                
            elseif isrowvector
                b = b';
            else
                sz = size(v);
                sz = [sz numel(b)/numel(v)];
                b = reshape(b,sz);
                
            end
        end
        
        function values = getValue(obj)
            v = obj.Indices;
            
            varIds = reshape(v,numel(v),1);
            
            beliefs = obj.VarMat.getBeliefs(varIds);
            
            
            domainIsScalars = 1;
            domain = zeros(1,length(obj.Domain));
            for i = 1:length(obj.Domain.Elements)
                if ~isscalar(obj.Domain.Elements{i})
                    domainIsScalars = 0;
                end
                domain(i) = obj.Domain.Elements{i};
            end
            
            if domainIsScalars
                %{
                TODO: this would almost work except for duplicate values.
                mxs = max(beliefs,[],2);
                mxs = repmat(mxs,1,length(domain));
                matches = mxs == beliefs;
                found = find(matches);
                [xs,ys] = ind2sub(size(matches),found);
                domain = repmat(domain,size(beliefs,1),1);
                values = domain(found);                
                %}
                values = zeros(size(beliefs,1),1);
                for i = 1:size(beliefs,1)
                    mx = max(beliefs(i,:));
                    firstIndex = find(beliefs(i,:)==mx);
                    firstIndex = firstIndex(1);
                    values(i) = obj.Domain.Elements{firstIndex};
                end
            else            
                values = cell(size(beliefs,1),1);
                for i = 1:size(beliefs,1)
                    mx = max(beliefs(i,:));
                    firstIndex = find(beliefs(i,:)==mx);
                    firstIndex = firstIndex(1);
                    values{i} = obj.Domain.Elements{firstIndex};
                end
            end
            
            if numel(values) == numel(v)
                values = reshape(values,size(v));
            elseif numel(v) > 1
                sz = size(values);
                sz = sz(1:(end)-1);
                sz = [sz size(v)];
                values = reshape(values,sz);
            end
            
            if ~domainIsScalars && numel(values) == 1
                values = values{1};
            end
            
        end
                
    end
    
end